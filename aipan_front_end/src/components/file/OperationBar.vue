<template>
  <el-row class="operation-bar" align="middle" justify="space-between">
    <el-col :span="16">
      <div v-if="isAllFiles && !isSelectionMode">
  <el-button 
    type="primary" 
    size="small" 
    class="glowing-btn upload-btn" 
    @click="openUploadDialog"
  >
    <el-icon><Upload /></el-icon>
    上传文件
  </el-button>
  <el-button 
    type="primary" 
    size="small" 
    class="glowing-btn folder-btn" 
    @click="openCreateFolderDialog"
  >
    <el-icon><Folder /></el-icon>
    新建文件夹
  </el-button>
</div>
<div v-if="isSelectionMode && selectedFiles.length > 0" class="button-group">
    <el-button class="custom-button primary" size="medium" @click="handleBatchDownload">
      <el-icon><Download /></el-icon>
      下载
    </el-button>
    <el-button class="custom-button danger" size="medium" @click="handleBatchDelete">
      <el-icon><Delete /></el-icon>
      删除
    </el-button>
    <el-button class="custom-button warning" size="medium" @click="handleMove">
      <el-icon><Position /></el-icon>
      移动
    </el-button>
    <el-button class="custom-button copy" size="medium" @click="handleCopy">
      <el-icon><DocumentCopy /></el-icon>
      复制
    </el-button>
    <el-button class="custom-button success" size="medium" @click="handleShare">
      <el-icon><Share /></el-icon>
      分享
    </el-button>
  </div>
    </el-col>
    <el-col :span="8" class="text-right">
      <div class="search-container">
      <el-input
        v-model="searchQuery"
        placeholder="搜索文件"
        class="search-input"
        @input="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>
      <el-space :size="4" class="right-actions">
        <el-tooltip content="刷新" placement="top">
          <el-button text @click="$emit('refresh')">
            <el-icon style="font-size: 24px;"><Refresh /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip
          v-if="viewMode === 'grid'"
          content="选择模式"
          placement="top"
        >
          <el-button
            text
            :class="{ 'is-active': isSelectionMode }"
            @click="toggleSelectionMode"
          >
            <el-icon style="font-size: 24px;"><Finished /></el-icon>
          </el-button>
        </el-tooltip>
        <el-divider direction="vertical" class="divider" />
        <el-tooltip content="列表视图" placement="top">
          <el-button
            text
            :class="{ 'is-active': viewMode === 'table' }"
            @click="handleViewModeChange('table')"
          >
            <el-icon style="font-size: 24px;"><List /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip content="网格视图" placement="top">
          <el-button
            text
            :class="{ 'is-active': viewMode === 'grid' }"
            @click="handleViewModeChange('grid')"
          >
            <el-icon style="font-size: 24px;"><Grid /></el-icon>
          </el-button>
        </el-tooltip>
      </el-space>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import {
  Delete,
  Download,
  Finished,
  Folder,
  Grid,
  List,
  Position,
  Refresh,
  Search,
  Share,
  DocumentCopy,
  Upload,
} from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { computed, ref } from "vue";
import { getFileTree } from "@/api/file";

const props = defineProps<{
  currentPath: string; // 当前文件夹路径
  selectedFiles: API.FileDTO[]; // 选中的文件列表
  fileType: number | undefined | null; // 文件类型过滤
  viewMode: string; // 视图模式：'table' | 'grid'
  isSelectionMode: boolean; // 是否处于多选模式
}>();

const emit = defineEmits([
  "refresh",
  "viewModeChange",
  "selectionModeChange",
  "openUploadDialog",
  "openCreateFolderDialog",
  "search",
  "batchDelete",
  "batchMove",
  "batchCopy",
  "shareFiles",
  "batchDownload",
]);

const searchQuery = ref("");

/**
 * 判断是否显示所有文件
 * 当 fileType 未定义或为空时显示所有文件
 */
const isAllFiles = computed(
  () => props.fileType === undefined || props.fileType === null
);

/**
 * 处理视图模式切换
 * @param newMode 新的视图模式：'table' | 'grid'
 */
const handleViewModeChange = (newMode: "table" | "grid") => {
  emit("viewModeChange", newMode);
};

/**
 * 切换选择模式
 * 在网格视图下可用，用于切换多选状态
 */
const toggleSelectionMode = () => {
  emit("selectionModeChange", !props.isSelectionMode);
};

/**
 * 处理批量下载
 * 当用户点击下载按钮时触发
 */
const handleBatchDownload = () => {
  ElMessage.success(`正在下载 ${props.selectedFiles.length} 个文件`);
  emit("batchDownload", props.selectedFiles);
};

/**
 * 处理批量删除
 * 弹出确认对话框，确认后触发删除事件
 */
const handleBatchDelete = () => {
  ElMessageBox.confirm(
    `确定要删除选中的 ${props.selectedFiles.length} 个文件吗？`,
    "警告",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    }
  )
    .then(() => {
      emit("batchDelete", props.selectedFiles);
    })
    .catch(() => {
      // 用户取消操作
    });
};

/**
 * 处理文件移动
 * 触发批量移动事件，传递选中的文件和当前路径
 */
const handleMove = () => {
  emit("batchMove", props.selectedFiles, props.currentPath);
  getFileTree();
};

const handleCopy = () => {
  emit("batchCopy", props.selectedFiles, props.currentPath);
  getFileTree();
};
/**
 * 处理文件分享
 * 触发分享事件，传递选中的文件列表
 */
const handleShare = () => {
  emit("shareFiles", props.selectedFiles);
};

/**
 * 打开上传对话框
 */
const openUploadDialog = () => {
  emit("openUploadDialog");
};

/**
 * 打开新建文件夹对话框
 */
const openCreateFolderDialog = () => {
  emit("openCreateFolderDialog");
};

/**
 * 处理搜索
 * 触发搜索事件，传递搜索关键词
 */
const handleSearch = () => {
  emit("search", searchQuery.value);
};
</script>

<style scoped>
.glowing-btn {
  position: relative;
  overflow: hidden;
  border-radius: 12px; /* 增加弧度 */
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  font-size: 18px;
}

.glowing-btn::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    115deg, 
    transparent,
    rgba(255, 255, 255, 0.3),
    transparent
  );
  transform: rotate(-45deg);
  animation: glowing 3s linear infinite;
}

.glowing-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 8px rgba(0, 0, 0, 0.15);
}

.upload-btn {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border-color: transparent;
  
}

.folder-btn {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border-color: transparent;
}

@keyframes glowing {
  0% {
    left: -50%;
    top: -50%;
  }
  100% {
    left: 150%;
    top: 150%;
  }
}


.operation-bar {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.text-right {
  text-align: right;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.right-actions {
  display: inline-flex;
  align-items: center;
}

.right-actions .el-button {
  padding: 6px;
  height: 32px;
  width: 32px;
}

.right-actions .el-button:hover {
  background-color: #f5f7fa;
}

.right-actions .el-button.is-active {
  color: #409eff;
  background-color: transparent;
}

.el-icon {
  font-size: 16px;
}

.divider {
  height: 16px;
  margin: 0 2px;
}

.el-space {
  --el-space-x-gap: 4px;
}


.el-space.right-actions {
  margin-left: 4px;
}
.button-group {
  display: flex;
  
}

.custom-button {
  border-radius: 25px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  padding: 7px 18px; /* 增加按钮的大小 */
  font-size: 15px; /* 增加字体大小 */
  background-size: 400% 400%;
  animation: gradient 5s ease infinite;
  margin-left: 5px;
  color: white;
}

.custom-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
}

.custom-button.primary {
  background: linear-gradient(45deg, #ff6ec4, #7873f5, #6b8dff, #65c7f7, #76e5fc);
}

.custom-button.danger {
  background: linear-gradient(45deg, #ff416c, #ff4b2b, #ff416c, #ff4b2b);
}

.custom-button.warning {
  background: linear-gradient(45deg, #ffba00, #ffc800, #ffba00, #ffc800);
}
.custom-button.copy {
  background: linear-gradient(45deg, #3ba2e7, #4cdcec, #69b8be, #47f3b7);
}
.custom-button.success {
  background: linear-gradient(45deg, #28a745, #218838, #28a745, #218838);
}

@keyframes gradient {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

.search-container {
  display: inline-block;
  transition: all 0.4s ease;
  padding: 5px;
}

.search-input {
  width: 400px; /* 初始宽度 */
  transition: all 0.4s ease;
  border-radius: 50px; /* 增加弧度 */
  border: 4px solid transparent; /* 加粗边框 */
  padding: 5px 10px;
}

.search-input input {
  width: 100%; /* 使input充满容器 */
  border: none;
  outline: none;
  background: #fff; /* 背景色为白色 */
}

.search-input:focus-within {
  width: 600px; /* 伸长后的宽度 */
}

.search-input input:focus {
  outline: none;
}

@keyframes gradient {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}
</style>
