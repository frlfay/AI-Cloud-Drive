<template>
  <div class="file-view flex flex-col h-full">
    <div class="flex-grow overflow-hidden">
      <OperationBar
        :currentPath="currentPath"
        :selectedFiles="selectedFiles"
        :fileType="fileType"
        :viewMode="fileStore.viewMode"
        :isSelectionMode="isSelectionMode"
        @refresh="refreshFiles"
        @viewModeChange="handleViewModeChange"
        @selectionModeChange="handleSelectionModeChange"
        @openUploadDialog="openUploadDialog"
        @openCreateFolderDialog="openCreateFolderDialog"
        @search="handleSearch"
        @batchDelete="handleBatchDelete"
        @batchMove="openMoveDialog"
        @batchCopy="openCopyDialog"
        @shareFiles="openShareDialog"
        @batchDownload="handleBatchDownload"
      />
      <BreadCrumb
        :currentPath="currentPath"
        :fileType="fileType"
        @pathChange="handlePathChange"
      />
      <div class="file-list-container overflow-auto">
        <FileTable
          v-if="fileStore.viewMode === 'table'"
          :key="'table-' + fileStore.viewMode"
          :fileList="fileList"
          :currentPath="currentPath"
          @refresh="refreshFiles"
          @fileClick="handleFileClick"
          @selectionChange="handleSelectionChange"
          @openRenameDialog="openRenameDialog"
          @moveFile="openMoveDialog"
          @copyFile="openCopyDialog"
          @deleteFile="handleDeleteFile"
          @shareFiles="openShareDialog"
          @openFileInfo="openFileInfo"
        />
        <FileGrid
          v-else
          :key="'grid-' + fileStore.viewMode"
          :fileList="fileList"
          :isSelectionMode="isSelectionMode"
          @refresh="refreshFiles"
          @fileClick="handleFileClick"
          @selectionChange="handleSelectionChange"
        />
      </div>
    </div>
    <div class="pagination-container border-t border-gray-200 py-4">
      <div class="pagination-wrapper">
        <span style="margin-top: 30px; color: blue" class="total-info">
          共 {{ total }} 条
        </span>
      </div>
    </div>

    <el-dialog v-model="uploadDialogVisible" title="上传文件" width="50%">
      <FileUpload
        :currentPath="currentPath"
        @upload-success="handleUploadSuccess"
      />
    </el-dialog>

    <el-dialog
      v-model="createFolderDialogVisible"
      title="新建文件夹"
      width="30%"
    >
      <el-form :model="folderForm" label-width="80px">
        <el-form-item label="文件夹名">
          <el-input
            v-model="folderForm.folderName"
            placeholder="请输入文件夹名"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createFolderDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCreateFolder">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改移动文件对话框 -->
    <el-dialog v-model="moveDialogVisible" title="移动文件" width="50%">
      <el-form :model="moveForm" label-width="80px">
        <el-form-item label="当前路径">
          <el-input v-model="moveForm.sourcePath" disabled></el-input>
        </el-form-item>
        <el-form-item label="目标路径">
          <el-input
            v-model="moveForm.targetPath"
            placeholder="请选择目标路径"
            readonly
          ></el-input>
        </el-form-item>
      </el-form>
      <FolderTree @select="handleFolderSelect" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="banch">取消</el-button>
          <el-button type="primary" @click="handleBatchMove">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 复制文件 -->
    <el-dialog v-model="CopyDialogVisible" title="复制文件" width="50%">
      <el-form :model="moveForm" label-width="80px">
        <el-form-item label="当前路径">
          <el-input v-model="moveForm.sourcePath" disabled></el-input>
        </el-form-item>
        <el-form-item label="目标路径">
          <el-input
            v-model="moveForm.targetPath"
            placeholder="请选择目标路径"
            readonly
          ></el-input>
        </el-form-item>
      </el-form>
      <FolderTree @select="handleFolderSelect" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="copy">取消</el-button>
          <el-button type="primary" @click="handleBatchCopy">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加分享对话框 -->
    <el-dialog v-model="shareDialogVisible" title="分享文件" width="30%">
      <el-form :model="shareForm" label-width="100px">
        <el-form-item label="分享名称">
          <el-input
            v-model="shareForm.shareName"
            placeholder="请输入分享名称"
          ></el-input>
        </el-form-item>
        <el-form-item label="分享类型">
          <el-select v-model="shareForm.shareType" placeholder="请选择分享类型">
            <el-option :value="no_code" label="公开分享"></el-option>
            <el-option :value="need_code" label="私密分享"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="有效期">
          <el-select
            v-model="shareForm.shareDayType"
            placeholder="请选择有效期"
          >
            <el-option :value="0" label="永久有效"></el-option>
            <el-option :value="1" label="7天"></el-option>
            <el-option :value="2" label="30天"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="shareDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCreateShare">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改分享结果对话框 -->
    <el-dialog v-model="shareResultDialogVisible" title="分享成功" width="40%">
      <el-form label-width="100px">
        <el-form-item label="分享链接：">
          <el-input v-model="shareUrl" readonly>
            <template #append>
              <el-button @click="copyToClipboard(shareUrl)">复制</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item v-if="shareResult.shareCode" label="分享码：">
          <el-input v-model="shareResult.shareCode" readonly>
            <template #append>
              <el-button @click="copyToClipboard(shareResult.shareCode)"
                >复制
              </el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="shareResultDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="copyAllShareInfo"
            >复制全部信息</el-button
          >
        </span>
      </template>
    </el-dialog>

    <!-- 添加重命名对话框 -->
    <el-dialog v-model="renameDialogVisible" title="重命名" width="30%">
      <el-form :model="renameForm" label-width="80px">
        <el-form-item label="新名称">
          <el-input
            v-model="renameForm.newFileName"
            placeholder="请输入新名称"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="renameDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRename">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 使用新的 FileInfo 组件 -->
    <FileInfo v-model="fileInfoDialogVisible" :fileInfo="fileInfo" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from "vue";
import { useLoginUserStore } from "@/store/user";
import { ElLoading, ElMessage } from "element-plus";
import FileTable from "@/components/file/FileTable.vue";
import FileGrid from "@/components/file/FileGrid.vue";
import BreadCrumb from "@/components/file/BreadCrumb.vue";
import OperationBar from "@/components/file/OperationBar.vue";
import FileUpload from "@/components/file/FileUpload.vue";
import {
  searchFilesByName,
  listFiles,
  createFolder,
  delFiles,
  moveFiles,
  copyFiles,
  renameFile,
  preview,
  downloadUrlParam,
  downloadFiles,
} from "@/api/file"; // 添加 renameFile
import { useFileStore } from "@/store/file";
import FolderTree from "@/components/file/FolderTree.vue";
import { createShare } from "@/api/share";
import FileInfo from "@/components/file/FileInfo.vue";
import { useStorageStore } from "@/store/storage";

const props = defineProps<{
  fileType: number | null;
}>();

const fileStore = useFileStore();
const no_code = ref("no_code");
const need_code = ref("need_code");
const fileList = ref<API.FileDTO[]>([]);
const currentPage = ref(1);
const { loginUser } = useLoginUserStore();
// const parent_id = ref(loginUser.rootFileId);
const parent_id = ref(localStorage.getItem("parent_id"));
const pageSize = ref(10);
const total = ref(0);
const currentPath = ref("/");
const fileType = ref<number | undefined>(undefined);
const isSelectionMode = ref(false);
const selectedFiles = ref<API.FileDTO[]>([]);
const uploadDialogVisible = ref(false);
const searchQuery = ref("");
const createFolderDialogVisible = ref(false);
// const accountId = Number(localStorage.getItem("accountId"));
const accountId = loginUser.id;
import { useFileStores } from "@/store/filePath";

const { filePaths } = useFileStores();
const shareUrl = ref("");

const folderForm = ref({
  folderName: "",
});
const moveDialogVisible = ref(false);
const CopyDialogVisible = ref(false);
const moveForm = ref({
  sourcePath: "",
  targetPath: "",
  targetParentId: "",
});
const shareDialogVisible = ref(false);
const shareResultDialogVisible = ref(false);
const shareForm = ref({
  shareName: "",
  shareType: "no_code",
  shareDayType: 0,
});
const shareResult = ref<API.ShareUrlDTO>({});

// 添加重命名相关的响应式变量
const renameDialogVisible = ref(false);
const renameForm = ref({
  fileId: undefined as string | undefined,
  accountId: loginUser.id,
  newFileName: "",
});

// 添加文件详情相关的响应式变量
const fileInfoDialogVisible = ref(false);
const fileInfo = ref<API.FileDTO>({});

const storageStore = useStorageStore();

/**
 * 根据条件查询文件列表
 */
async function searchFiles() {
  try {
    const response = await searchFilesByName({
      search: searchQuery.value,
    });

    console.log("API Response:", response); // 查看实际响应

    if (!response.data) {
      ElMessage.error("API 响应为空");
      return;
    }

    if (!response.data.success) {
      ElMessage.error(response.data.msg || "API 请求失败");
      return;
    }

    if (!response.data.data || !Array.isArray(response.data.data)) {
      ElMessage.error("API 数据格式不正确");
      return;
    }

    fileList.value = response.data.data.map((file: API.FileDTO) => ({
      ...file,
      fileSuffix: file.isDir === 1 ? "folder" : file.fileSuffix || "other",
      fileType: file.isDir === 1 ? "folder" : file.fileType || "other",
    }));

    total.value = response.data.data.length || 0;
  } catch (error) {
    console.error("获取文件列表出错:", error);
    if (error instanceof Error) {
      ElMessage.error(`获取文件列表失败: ${error.message}`);
    } else {
      ElMessage.error("获取文件列表失败");
    }
  }
}

/**
 * 获取文件列表
 */
async function fetchFiles() {
  try {
    const response = await listFiles({
      parent_id: parent_id.value,
    });

    console.log("API Response:", response); // 查看实际响应

    if (!response.data) {
      ElMessage.error("API 响应为空");
      return;
    }

    if (!response.data.success) {
      ElMessage.error(response.data.msg || "API 请求失败");
      return;
    }

    if (!response.data.data || !Array.isArray(response.data.data)) {
      ElMessage.error("API 数据格式不正确");
      return;
    }

    fileList.value = response.data.data.map((file: API.FileDTO) => ({
      ...file,
      fileSuffix: file.isDir === 1 ? "folder" : file.fileSuffix || "other",
      fileType: file.isDir === 1 ? "folder" : file.fileType || "other",
    }));

    total.value = response.data.data.length || 0;
  } catch (error) {
    console.error("获取文件列表出错:", error);
    if (error instanceof Error) {
      ElMessage.error(`获取文件列表失败: ${error.message}`);
    } else {
      ElMessage.error("获取文件列表失败");
    }
  }
}

onMounted(() => {
  fetchFiles();
});

const refreshFiles = () => {
  fetchFiles();
};

const handleSizeChange = (val: number) => {
  pageSize.value = val;
  fetchFiles();
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  fetchFiles();
};

/**
 * 点击文件夹
 */
const handleFileClick = (file: API.FileDTO) => {
  if (file.isDir === 1) {
    console.log("前路径:", currentPath);
    currentPath.value =
      currentPath.value === "/"
        ? `/${file.fileName}/`
        : `${currentPath.value}${file.fileName}/`;
    console.log("后路径:", currentPath);
    parent_id.value = String(file.id);
    currentPage.value = 1;
    fetchFiles();
  } else {
    console.log("File clicked:", file);
  }
};

const handlePathChange = (newPath: string, ID: string) => {
  currentPath.value = newPath;
  parent_id.value = ID;
  currentPage.value = 1;
  const startIndex = filePaths.indexOf(ID);
  if (startIndex !== -1) {
    useFileStores().filePaths = filePaths.slice(0, startIndex);
  }
  fetchFiles();
};

/**
 * 切换视图模式
 */
const handleViewModeChange = (newMode: "table" | "grid") => {
  fileStore.setViewMode(newMode);
  isSelectionMode.value = false;
  selectedFiles.value = [];
};

const handleSelectionModeChange = (mode: boolean) => {
  isSelectionMode.value = mode;
  if (!mode) {
    selectedFiles.value = [];
  }
};

const handleSelectionChange = (selection: API.FileDTO[]) => {
  selectedFiles.value = selection;
  isSelectionMode.value = selection.length > 0;
};

const startRecord = computed(() => {
  return (currentPage.value - 1) * pageSize.value + 1;
});

const endRecord = computed(() => {
  return Math.min(currentPage.value * pageSize.value, total.value);
});

watch(
  () => props.fileType,
  (newFileType) => {
    fileType.value = newFileType ?? undefined;
    currentPath.value = "/";
    currentPage.value = 1;
    fetchFiles();
  },
  { immediate: true }
);

const openUploadDialog = () => {
  uploadDialogVisible.value = true;
};

const handleUploadSuccess = ({ file, path }: { file: any; path: string }) => {
  refreshFiles();
  storageStore.updateStorageInfo();
};

const handleSearch = (query: string) => {
  searchQuery.value = query;
  currentPage.value = 1; // 重置页码
  if (searchQuery.value == null || searchQuery.value == "") {
    fetchFiles();
  } else {
    searchFiles();
  }
};

const openCreateFolderDialog = () => {
  createFolderDialogVisible.value = true;
};

const handleCreateFolder = async () => {
  if (!folderForm.value.folderName) {
    ElMessage.warning("请输入文件夹名");
    return;
  }
  console.log(folderForm.value.folderName);
  try {
    const response = await createFolder({
      folderName: folderForm.value.folderName,
      parentId: String(parent_id.value),
      accountId: accountId,
    });
    console.log("文件ID:", parent_id.value);
    if (response.data && response.data.success) {
      ElMessage.success("文件夹创建成功");
      createFolderDialogVisible.value = false;
      folderForm.value.folderName = "";
      refreshFiles();
    } else {
      ElMessage.error(response.data?.msg || "创建文件夹失败");
    }
  } catch (error) {
    console.error("创建文件夹出错:", error);
    ElMessage.error("创建文件夹失败");
  }
};

/**
 * 批量下载
 */
const handleBatchDownload = async (filesToDownload: API.FileDTO[]) => {
  try {
    const fileIds = filesToDownload.map((file) => String(file.id));

    if (fileIds.length > 1 || filesToDownload[0].isDir == 1) {
      const response = await downloadFiles({
        fileIds,
        accountId,
      });
      const blob = response.data;
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = "download.zip";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } else {
      const response = await downloadUrlParam({
        fileIds,
      });
      if (response.data && response.data.success) {
        console.log("文件下载连接：", response.data.data);
        const downloadUrls = response.data.data;
        for (let i = 0; i < downloadUrls.length; i++) {
          // if (Number(filesToDownload[i].fileSize) < 5242880) {
          fetch(downloadUrls[i].downloadUrl)
            .then((response) => response.blob())
            .then((blob) => {
              // 使用a标签模拟下载
              const link = document.createElement("a");
              const url = URL.createObjectURL(blob);
              link.href = url;
              // 设置下载的文件名
              link.download = String(downloadUrls[i].fileName);
              document.body.appendChild(link);
              link.click(); // 触发下载
              window.URL.revokeObjectURL(url); // 释放 URL 对象
              document.body.removeChild(link); // 移除链接元素
            });
        }
      }
    }
  } catch (error) {
    console.error("下载文件出错:", error);
    ElMessage.error("下载文件失败");
  }
};

const handleBatchDelete = async (filesToDelete: API.FileDTO[]) => {
  try {
    const fileIds = filesToDelete.map((file) => String(file.id));

    // 将 fileIds 和 accountId 合并为一个对象
    const requestBody = {
      accountId,
      fileIds, // 确保 accountId 是响应式数据的值
    };

    const response = await delFiles(requestBody);

    if (response.data && response.data.success) {
      ElMessage.success("文件删除成功");
      refreshFiles();
      isSelectionMode.value = false;
      selectedFiles.value = [];
      storageStore.updateStorageInfo();
    } else {
      ElMessage.error(response.data?.msg || "删除文件失败");
    }
  } catch (error) {
    console.error("删除文件出错:", error);
    ElMessage.error("删除文件失败");
  }
};
const openMoveDialog = (filesToMove: API.FileDTO[], sourcePath: string) => {
  moveDialogVisible.value = true;
  selectedFiles.value = filesToMove;
  moveForm.value.sourcePath = sourcePath;
  moveForm.value.targetPath = sourcePath; // 默认设置为当前路径，用户可以修改
};

const openCopyDialog = (filesToMove: API.FileDTO[], sourcePath: string) => {
  CopyDialogVisible.value = true;
  selectedFiles.value = filesToMove;
  moveForm.value.sourcePath = sourcePath;
  moveForm.value.targetPath = sourcePath; // 默认设置为当前路径，用户可以修改
};

const copy = async () => {
  CopyDialogVisible.value = false;
  // location.reload();
};

const handleBatchCopy = async () => {
  if (!moveForm.value.targetPath) {
    ElMessage.warning("请输入目标路径");
    return;
  }

  try {
    const fileIds = selectedFiles.value.map((file) => String(file.id));
    const { filepath } = useFileStores();
    const targetParentId = filepath;
    const response = await copyFiles({
      fileIds,
      accountId,
      targetParentId,
    });
    if (response.data && response.data.success) {
      ElMessage.success("文件复制成功");
      storageStore.updateStorageInfo();
      CopyDialogVisible.value = false;
      moveForm.value.sourcePath = "";
      moveForm.value.targetPath = "";
      // refreshFiles();
      // location.reload();
    } else {
      ElMessage.error(response.data?.msg || "复制文件失败");
    }
  } catch (error) {
    console.error("复制文件出错:", error);
    ElMessage.error("复制文件失败");
  }
};

const banch = () => {
  moveDialogVisible.value = false;
  // location.reload();
};

const handleBatchMove = async () => {
  if (!moveForm.value.targetPath) {
    ElMessage.warning("请输入目标路径");
    return;
  }

  try {
    const fileIds = selectedFiles.value.map((file) => String(file.id));
    const { filepath } = useFileStores();
    const targetParentId = filepath;
    const response = await moveFiles({
      fileIds,
      accountId,
      targetParentId,
    });

    if (response.data && response.data.success) {
      ElMessage.success("文件移动成功");
      moveDialogVisible.value = false;
      moveForm.value.sourcePath = "";
      moveForm.value.targetPath = "";
      refreshFiles();
      isSelectionMode.value = false;
      selectedFiles.value = [];
      // location.reload();
    } else {
      ElMessage.error(response.data?.msg || "移动文件失败");
    }
  } catch (error) {
    console.error("移动文件出错:", error);
    ElMessage.error("移动文件失败");
  }
};

const handleFolderSelect = (node: API.TreeNodeDTO & { path: string }) => {
  moveForm.value.targetPath = node.path;
  useFileStores().setFilePath(node.id);
};

const openShareDialog = (filesToShare: API.FileDTO[]) => {
  shareDialogVisible.value = true;
  selectedFiles.value = filesToShare;
  shareForm.value.shareName =
    filesToShare.length === 1
      ? filesToShare[0].fileName ?? ""
      : `${filesToShare[0].fileName ?? ""}等${filesToShare.length}个文件`;
};

const handleCreateShare = async () => {
  try {
    const fileIds = selectedFiles.value.map((file) => String(file.id));
    const host = window.location.host;
    const response = await createShare({
      ...shareForm.value,
      fileIds,
      accountId,
      host,
    });

    if (response.data && response.data.success) {
      shareResult.value = response.data.data as API.ShareUrlDTO;
      //'47.119.128.20:8080'
      // shareUrl.value = String(shareResult.value.shareUrl?.replace(shareResult.value.shareUrl?.toString().split("/")[2], window.location.host))
      shareUrl.value = String(shareResult.value.shareUrl);
      shareDialogVisible.value = false;
      shareResultDialogVisible.value = true;
    } else {
      ElMessage.error(response.data?.msg || "创建分享失败");
    }
  } catch (error) {
    console.error("创建分享出错:", error);
    ElMessage.error("创建分享失败");
  }
};

// const copyToClipboard = (text: string | undefined) => {
//   if (text) {
//     navigator.clipboard
//       .writeText(text)
//       .then(() => {
//         ElMessage.success("复制成功");
//       })
//       .catch(() => {
//         ElMessage.error("复制失败，请手动复制");
//       });
//   }
// };

const copyToClipboard = (text: string | undefined) => {
  if (!text) {
    ElMessage.warning("没有可复制的文本");
    return;
  }

  try {
    // 创建一个临时的 textarea 元素
    const textArea = document.createElement("textarea");
    textArea.value = text;
    document.body.appendChild(textArea);

    // 选择文本框中的内容
    textArea.select();
    textArea.setSelectionRange(0, 99999); // 移动光标到文本末尾

    // 执行复制命令
    let successful = document.execCommand("copy");
    let msg = successful ? "复制成功" : "复制失败";

    // 移除临时的 textarea 元素
    document.body.removeChild(textArea);

    if (successful) {
      ElMessage.success(msg);
    } else {
      throw new Error(msg);
    }
  } catch (err) {
    console.error("Failed to copy text: ", err);
    ElMessage.error("复制失败，请手动复制");
  }
};

const copyAllShareInfo = () => {
  const shareInfo = `分享链接：${shareResult.value.shareUrl}\n${
    shareResult.value.shareCode ? `分享码：${shareResult.value.shareCode}` : ""
  }`;
  copyToClipboard(shareInfo);
};

// 实现重命名相关的方法
const openRenameDialog = (file: API.FileDTO) => {
  renameDialogVisible.value = true;
  renameForm.value = {
    fileId: file.id,
    newFileName: file.fileName || "",
    accountId: loginUser.id,
  };
};

const handleRename = async (file: API.FileDTO) => {
  if (!renameForm.value.newFileName) {
    ElMessage.warning("请输入新名称");
    return;
  }

  try {
    const response = await renameFile({
      fileId: renameForm.value.fileId,
      newFilename: renameForm.value.newFileName,
      accountId: renameForm.value.accountId,
    });
    console.log("新文件名：", renameForm.value.newFileName);
    file.fileName = renameForm.value.newFileName;
    if (response.data && response.data.success) {
      ElMessage.success("重命名成功");

      refreshFiles();
      renameDialogVisible.value = false;
    } else {
      ElMessage.error(response.data?.msg || "重命名失败");
    }
  } catch (error) {
    console.error("重命名出错:", error);
    ElMessage.error("重命名失败");
  }
};

// 实现删除单个文件的方法
const handleDeleteFile = async (file: API.FileDTO) => {
  try {
    // const fileIds = filesToDelete.map((file) => String(file.id));
    const requestBody = {
      accountId,
      fileIds: [String(file.id)],
    };

    const response = await delFiles(requestBody);

    if (response.data && response.data.success) {
      ElMessage.success("文件删除成功");
      refreshFiles();
      storageStore.updateStorageInfo();
    } else {
      ElMessage.error(response.data?.msg || "删除文件失败");
    }
  } catch (error) {
    console.error("删除文件出错:", error);
    ElMessage.error("删除文件失败");
  }
};

// 添加格式化文件大小的函数
const formatFileSize = (size: number | undefined) => {
  if (size == null) return "-";
  const units = ["B", "KB", "MB", "GB", "TB"];
  let index = 0;
  let sizeNum = size;
  while (sizeNum >= 1024 && index < units.length - 1) {
    sizeNum /= 1024;
    index++;
  }
  return `${sizeNum.toFixed(2)} ${units[index]}`;
};

// 添加格式化日期时间的函数
const formatDateTime = (timestamp: number | undefined) => {
  if (!timestamp) return "-";
  return new Date(timestamp).toLocaleString();
};

// 添加打开文件详情的方法
const openFileInfo = (file: API.FileDTO) => {
  fileInfo.value = file;
  fileInfoDialogVisible.value = true;
};
</script>

<style scoped>
.file-view {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.file-list-container {
  flex-grow: 1;
  overflow-y: auto;
}

.pagination-container {
  background-color: white;
  margin-top: auto;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.total-info {
  position: absolute;
  left: auto;
  font-size: 18px;
}

:deep(.el-pagination) {
  justify-content: center;
}
</style>
