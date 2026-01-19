<template>
  <div class="recycle-view">
    <div class="operation-bar">
      <el-button
        class="dynamic-button restore-button"
        type="primary"
        :disabled="!selectedFiles.length"
        @click="handleBatchRestore"
      >
        <el-icon>
          <RefreshLeft />
        </el-icon>
        还原{{ selectedFiles.length ? `(${selectedFiles.length}个文件)` : "" }}
      </el-button>

      <!-- 删除按钮 -->
      <el-button
        class="dynamic-button delete-button"
        type="danger"
        :disabled="!selectedFiles.length"
        @click="handleBatchDelete"
      >
        <el-icon>
          <Delete />
        </el-icon>
        彻底删除{{
          selectedFiles.length ? `(${selectedFiles.length}个文件)` : ""
        }}
      </el-button>
    </div>

    <FileTable :fileList="fileList" @selection-change="handleSelectionChange">
      <template #operation="{ row }">
        <el-button-group>
          <el-tooltip effect="dark" content="还原" placement="top">
            <el-button type="primary" link @click="handleRestore(row)">
              <el-icon>
                <RefreshLeft />
              </el-icon>
            </el-button>
          </el-tooltip>
          <el-tooltip effect="dark" content="彻底删除" placement="top">
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon>
                <Delete />
              </el-icon>
            </el-button>
          </el-tooltip>
        </el-button-group>
      </template>
    </FileTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Delete, RefreshLeft } from "@element-plus/icons-vue";
import { listRecycleFiles, batchRestore, batchDelete } from "@/api/recycle";
import FileTable from "@/components/file/FileTable.vue";
import { useStorageStore } from "@/store/storage";

const fileList = ref<API.FileDTO[]>([]);
const selectedFiles = ref<API.FileDTO[]>([]);
const storageStore = useStorageStore();

// 获取回收站文件列表
const fetchRecycleFiles = async () => {
  try {
    const response = await listRecycleFiles();
    if (response.data?.success) {
      fileList.value = (response.data?.data ?? []).map((file: API.FileDTO) => ({
        ...file,
        fileType: file.isDir === 1 ? "folder" : file.fileType || "other",
        fileSuffix: file.isDir === 1 ? "folder" : file.fileSuffix || "other",
      }));
    } else {
      ElMessage.error(response.data?.msg || "获取回收站文件列表失败");
    }
  } catch (error) {
    console.error("获取回收站文件列表失败:", error);
    ElMessage.error("获取回收站文件列表失败");
  }
};

// 处理文件选择
const handleSelectionChange = (selection: API.FileDTO[]) => {
  selectedFiles.value = selection;
};

// 还原单个文件
const handleRestore = async (file: API.FileDTO) => {
  try {
    const response = await batchRestore({
      fileIds: [String(file.id)],
    });

    if (response.data?.success) {
      ElMessage.success("文件还原成功");
      fetchRecycleFiles();
      storageStore.updateStorageInfo();
    } else {
      ElMessage.error(response.data?.msg || "文件还原失败");
    }
  } catch (error) {
    console.error("文件还原失败:", error);
    ElMessage.error("文件还原失败");
  }
};

// 批量还原文件
const handleBatchRestore = async () => {
  if (!selectedFiles.value.length) return;

  try {
    const response = await batchRestore({
      fileIds: selectedFiles.value.map((file) => String(file.id)),
    });

    if (response.data?.success) {
      ElMessage.success("文件还原成功");
      fetchRecycleFiles();
      selectedFiles.value = [];
      storageStore.updateStorageInfo();
    } else {
      ElMessage.error(response.data?.msg || "文件还原失败");
    }
  } catch (error) {
    console.error("文件还原失败:", error);
    ElMessage.error("文件还原失败");
  }
};

// 彻底删除单个文件
const handleDelete = async (file: API.FileDTO) => {
  try {
    await ElMessageBox.confirm("此操作将永久删除该文件, 是否继续?", "警告", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const response = await batchDelete({
      fileIds: [String(file.id)],
    });

    if (response.data?.success) {
      ElMessage.success("文件删除成功");
      fetchRecycleFiles();
    } else {
      ElMessage.error(response.data?.msg || "文件删除失败");
    }
  } catch (error) {
    if (error !== "cancel") {
      console.error("文件删除失败:", error);
      ElMessage.error("文件删除失败");
    }
  }
};

// 批量彻底删除文件
const handleBatchDelete = async () => {
  if (!selectedFiles.value.length) return;

  try {
    await ElMessageBox.confirm(
      "此操作将永久删除选中的文件, 是否继续?",
      "警告",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    const response = await batchDelete({
      fileIds: selectedFiles.value.map((file) => String(file.id)),
    });

    if (response.data?.success) {
      ElMessage.success("文件删除成功");
      fetchRecycleFiles();
      selectedFiles.value = [];
    } else {
      ElMessage.error(response.data?.msg || "文件删除失败");
    }
  } catch (error) {
    if (error !== "cancel") {
      console.error("文件删除失败:", error);
      ElMessage.error("文件删除失败");
    }
  }
};

onMounted(() => {
  fetchRecycleFiles();
});
</script>

<style scoped>
.recycle-view {
  /* 删除 padding: 20px; */
}

.operation-bar {
  padding: 0 20px;
  margin-bottom: 20px;
}

.dynamic-button {
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  font-weight: bold;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

/* 渐变背景 - 还原按钮 */
.restore-button {
  background: linear-gradient(45deg, #4caf50, #81c784);
  color: #fff;
}

/* 渐变背景 - 删除按钮 */
.delete-button {
  background: linear-gradient(45deg, #e53935, #ff5252);
  color: #fff;
}

/* 鼠标悬停效果：按钮放大一点并加阴影 */
.dynamic-button:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

/* 波动动画的关键帧 */
@keyframes pulse {
  0% {
    transform: scale(1);
    filter: brightness(1);
  }

  50% {
    transform: scale(1.03);
    filter: brightness(1.1);
  }

  100% {
    transform: scale(1);
    filter: brightness(1);
  }
}

/* 未禁用时添加波动动画 */
.dynamic-button:not([disabled]) {
  animation: pulse 1.5s infinite;
}

/* 禁用状态的样式 */
.dynamic-button[disabled] {
  background: #e0e0e0;
  /* 灰色背景表示禁用 */
  color: #9e9e9e;
  /* 灰色文字 */
  cursor: not-allowed;
  animation: none;
  /* 禁用时无动画 */
  box-shadow: none;
}
</style>