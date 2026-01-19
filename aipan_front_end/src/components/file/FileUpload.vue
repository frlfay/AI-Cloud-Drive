<template>
  <div class="file-upload">
    <el-upload
      class="upload-demo"
      drag
      action="/"
      multiple
      :auto-upload="false"
      :on-change="handleFileChange"
      :on-remove="handleRemoveFile"
      :file-list="fileList"
    >
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">
        请拖拽文件到此处或 <em>点击此处上传</em>
      </div>
    </el-upload>
    <el-button type="primary" @click="startUpload" style="margin-top: 16px">开始上传</el-button>
  </div>
</template>

<script setup>
import {
  initTask,
  merge,
  preSignUploadUrl,
  taskInfo,
  uploadFiles,
  secUpload,
} from "@/api/file";
import md5 from "@/utils/md5";
import { UploadFilled } from "@element-plus/icons-vue";
import axios from "axios";
import { ElNotification } from "element-plus";
import Queue from "promise-queue-plus";
import { defineEmits, defineProps, ref, onMounted, onBeforeUnmount } from "vue";
import { useLoginUserStore } from "@/store/user";

const props = defineProps({
  currentPath: {
    type: String,
    required: true,
  },
});

const { loginUser } = useLoginUserStore();
const parentId = localStorage.getItem("parent_id");
const accountId = useLoginUserStore().loginUser.id;
const fileList = ref([]);
const fileUploadChunkQueue = ref({});
const emit = defineEmits(["upload-progress", "upload-success", "upload-error"]);

onBeforeUnmount(() => {
 fileList.value = [];
});



// 获取或创建上传任务信息
const getTaskInfo = async (file) => {

  let task;
  const identifier = await md5(file);
  const res = await taskInfo({ identifier });
  const { code, data, msg } = res.data;

  if (file.size <= 1024 * 1024 * 2) {
    // 对于小于5MB的文件，直接上传
    return handleSmallFileUpload(file, identifier);
  }
  console.log("111111111111111111111111111111111111",res.data)
  if (code === 0 && data) {
    task = {
        ...data,
        taskRecord: {
          exitPartList: data.exitPartList,
          chunkSize: data.chunkSize,
          chunkNum: data.chunkNum,
          fileIdentifier: identifier,
        },
      };
  } else {
    // 初始化新任务
    const initTaskData = {
      accountId,
      identifier,
      filename: file.name,
      totalSize: file.size,
      chunkSize: 5 * 1024 * 1024,
    };
    const initRes = await initTask(initTaskData);

    if (initRes.data.code === 0) {
      task = {
        ...initRes.data.data,
        taskRecord: {
          exitPartList: [],
          chunkSize: initTaskData.chunkSize,
          chunkNum: Math.ceil(file.size / initTaskData.chunkSize),
          fileIdentifier: identifier,
        },
      };
    } else {
      ElNotification.error({ title: "文件上传错误", message: initRes.data.msg });
    }
  }

  return task || null;
};

// 处理小于5MB的文件上传
const handleSmallFileUpload = async (file, identifier) => {
  try {
    const response = await uploadFiles({
      file: file,
      identifier,
      filename: file.name,
      accountId,
      parentId: localStorage.getItem("parent_id"),
      fileSize: file.size,
    });

    if (response.data.code === 0) {
      emit("upload-success", { file });
      ElNotification.success({
        title: "上传成功",
        message: `文件 ${file.name} 上传成功`,
      });

      fileList.value = fileList.value.filter(f => f.uid !== file.uid);
      return null; // 表示已经完成上传，不再需要进一步处理
    } else {
      throw new Error(response.data.msg || "上传文件失败");
    }
  } catch (error) {
    fileList.value = [];
    ElNotification.error({ title: "文件上传错误", message: error.message });
  }
};

// 处理文件上传
const handleUpload = async (file, taskRecord, options) => {
  let lastUploadedSize = 0;
  const totalSize = file.size || 0;
  const { exitPartList, chunkSize, chunkNum, fileIdentifier } = taskRecord;

  for (let partNumber = 1; partNumber <= chunkNum; partNumber++) {
    const exitPart = (exitPartList || []).find((part) => part.partNumber == partNumber);

    if (!exitPart) {
      await uploadNext(partNumber, file, fileIdentifier, chunkSize, options.onProgress);
    } else {
      lastUploadedSize += Number(exitPart.size);
      options.onProgress({ percent: Math.ceil((lastUploadedSize / totalSize) * 100)  });
    }
  }
};

// 上传单个分片
const uploadNext = async (partNumber, file, fileIdentifier, chunkSize, onProgress) => {
  try {
    const start = Number(chunkSize) * (partNumber - 1);
    const end = (file.size -start) > chunkSize ? start + Number(chunkSize) : start + (file.size -start);
    const blob = file.slice(start, end);
    const res = await preSignUploadUrl({ identifier: fileIdentifier, partNumber });

    if (res.data.code === 0 && res.data.data) {
      await axios.request({
        url: res.data.data,
        method: "PUT",
        data: blob,
        headers: { "Content-Type": "application/octet-stream" },
      });
      onProgress({ percent: Math.ceil((end / file.size) * 100)  });
    } else {
      throw new Error(res.data.msg || `获取分片${partNumber}的上传地址失败`);
    }
  } catch (error) {
    console.error(`分片${partNumber}上传失败:`, error);
    throw error;
  }
};

// 处理文件变更事件
const handleFileChange = (file, uploadFileList) => {
  file.percentage = 0;
  file.status = "ready";
  fileList.value = uploadFileList;
};

// 处理单个文件的上传
const handleSingleFileUpload = async (file) => {
  file.status = "uploading";
  emit("upload-progress", { file, progress: 0 });

  const identifier = await md5(file.raw);
  const res = await secUpload({
    identifier,
    filename: file.name,
    accountId,
    parentId: localStorage.getItem("parent_id"),
  });

  if (res.data.data !== false) {
    emit("upload-success", { file });
    ElNotification.success({
      title: "上传成功",
      message: `文件 ${file.name} 上传成功`,
    });
    fileList.value = fileList.value.filter(f => f.uid !== file.uid);
    return;
  }

  let task = await getTaskInfo(file.raw);
  if (task === null) {
    return;
  }

  if (!task || task.finished) {
    emit("upload-success", { file, path: `${props.currentPath}${file.name}` });
    ElNotification.success({
      title: "上传成功",
      message: `文件 ${file.name} 上传成功`,
    });
    fileList.value = fileList.value.filter(f => f.uid !== file.uid);
    return;
  }

  try {
    await handleUpload(file.raw, task.taskRecord, {
      onProgress: (progress) => {
        file.percentage = progress.percent;
        emit("upload-progress", { file, progress: progress.percent });
      },
    });

    const mergeRes = await merge({
      identifier: task.taskRecord.fileIdentifier,
      accountId,
      parentId: localStorage.getItem("parent_id"),
    });

    if (mergeRes.data.code === 0) {
      file.status = "success";
      file.percentage = 100;
      emit("upload-success", { file, path: `${props.currentPath}${file.name}` });
      ElNotification.success({
        title: "上传成功",
        message: `文件 ${file.name} 上传成功`,
      });
      fileList.value = fileList.value.filter(f => f.uid !== file.uid);
    } else {
      throw new Error(mergeRes.data.msg || "文件合并失败");
    }
  } catch (error) {
    file.status = "error";
    emit("upload-error", { file, error: error.message });
    ElNotification.error({
      title: "上传失败",
      message: error.message,
    });
  }
};

// 开始上传所有文件
const startUpload = async () => {
  if (fileList.value.length === 0) {
    ElNotification.warning({
      title: "上传提示",
      message: "请先选择要上传的文件",
    });
    return;
  }

  for (const file of fileList.value) {

    if (file.status !== "success") {
      await handleSingleFileUpload(file);
    }
  }
};

// 处理文件移除事件
const handleRemoveFile = (file) => {
  const queueObject = fileUploadChunkQueue.value[file.uid];
  if (queueObject) {
    queueObject.stop();
    fileUploadChunkQueue.value[file.uid] = undefined;
    //  fileList.value = fileList.value.filter(f => f !== file);
  }

  fileList.value = fileList.value.filter((f) => f.uid !== file.uid);
};
</script>
