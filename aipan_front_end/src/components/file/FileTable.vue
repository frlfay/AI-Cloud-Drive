<template>
  <el-table
    :data="fileList"
    style="width: 100%"
    @selection-change="handleSelectionChange"
    :row-class-name="tableRowClassName"
  >
    <el-table-column type="selection" width="55" />
    <el-table-column prop="fileName" label="文件名" min-width="300">
      <template #default="scope">
        <div
          class="file-name"
          @click="handleFileClick(scope.row)"
          @contextmenu.prevent="handleContextMenu($event, scope.row)"
        >
          <img :src="getFileIcon(scope.row.fileSuffix)" class="file-icon" />
          <span class="file-name-text">{{ scope.row.fileName }}</span>
        </div>
      </template>
    </el-table-column>
    <el-table-column prop="fileType" label="类型" width="120">
      <template #default="scope">
        {{ getFileTypeName(scope.row.fileType) }}
      </template>
    </el-table-column>
    <el-table-column prop="fileSize" label="大小" width="120" align="right">
      <template #default="scope">
        {{ formatFileSize(scope.row.fileSize) }}
      </template>
    </el-table-column>
    <el-table-column
      prop="gmtModified"
      label="修改日期"
      width="180"
      align="right"
    >
      <template #default="scope">
        {{ formatDate(scope.row.gmtModified) }}
      </template>
    </el-table-column>
  </el-table>

  <ContextMenu
    v-model:visible="showContextMenu"
    :x="contextMenuPosition.x"
    :y="contextMenuPosition.y"
    @action="handleContextMenuAction"
  />
</template>

<script setup lang="ts">
import { ref } from "vue";
import { getFileIcon } from "@/utils/fileIcons";
import ContextMenu from "./ContextMenu.vue";

const props = defineProps<{
  fileList: API.FileDTO[];
  currentPath?: string;
}>();

const emit = defineEmits([
  "fileClick",
  "selectionChange",
  "openRenameDialog",
  "moveFile",
  "deleteFile",
  "shareFiles",
  "openFileInfo",
  "copyFile",
]);

/**
 * 格式化文件大小显示
 * 将字节大小转换为人类可读的格式（B、KB、MB、GB、TB）
 * @param size 文件大小
 * @returns 格式化后的文件大小字符串
 */
const formatFileSize = (size: string | number | null | undefined) => {
  if (size == null) return "-";
  let sizeNum = typeof size === "string" ? parseFloat(size) : size;
  if (typeof sizeNum !== "number" || isNaN(sizeNum)) return "-";
  const units = ["B", "KB", "MB", "GB", "TB"];
  let index = 0;
  while (sizeNum >= 1024 && index < units.length - 1) {
    sizeNum /= 1024;
    index++;
  }
  return `${sizeNum.toFixed(2)} ${units[index]}`;
};

/**
 * 格式化日期显示
 * @param timestamp 时间戳
 * @returns 本地化的日期时间字符串
 */
const formatDate = (timestamp: number) => {
  return new Date(timestamp).toLocaleString();
};

/**
 * 获取文件类型的显示名称
 * @param fileType 文件类型
 * @returns 格式化后的文件类型名称
 */
const getFileTypeName = (fileType: string | null | undefined) => {
  if (!fileType) return "-";
  if (fileType === "folder") return "文件夹";
  return fileType.toUpperCase();
};

// 右键菜单相关状态
const showContextMenu = ref(false);
const contextMenuPosition = ref({ x: 0, y: 0 });
const selectedFile = ref<API.FileDTO | null>(null);

/**
 * 处理右键菜单事件
 * @param event 鼠标事件
 * @param file 当前操作的文件对象
 */
const handleContextMenu = (event: MouseEvent, file: API.FileDTO) => {
  event.preventDefault();
  showContextMenu.value = true;
  contextMenuPosition.value = { x: event.clientX, y: event.clientY };
  selectedFile.value = file;
};

/**
 * 处理右键菜单选项点击
 * @param action 操作类型：'view'|'move'|'share'|'rename'|'info'|'delete'
 */
const handleContextMenuAction = (action: string) => {
  switch (action) {
    case "view":
      if (selectedFile.value) {
        handleFileClick(selectedFile.value);
      }
      break;
    case "copy":
      if (selectedFile.value) {
        emit("copyFile", [selectedFile.value], props.currentPath || "/");
      }
      break;
    case "move":
      if (selectedFile.value) {
        emit("moveFile", [selectedFile.value], props.currentPath || "/");
      }
      break;
    case "share":
      if (selectedFile.value) {
        emit("shareFiles", [selectedFile.value]);
      }
      break;
    case "rename":
      if (selectedFile.value) {
        emit("openRenameDialog", selectedFile.value);
      }
      break;
    case "info":
      if (selectedFile.value) {
        emit("openFileInfo", selectedFile.value);
      }
      break;
    case "delete":
      if (selectedFile.value) {
        emit("deleteFile", selectedFile.value);
      }
      break;
  }
};

/**
 * 处理文件点击事件
 * @param file 被点击的文件对象
 */
const handleFileClick = (file: API.FileDTO) => {
  emit("fileClick", file);
};

/**
 * 处理表格选择变化事件
 * @param selection 当前选中的文件列表
 */
const handleSelectionChange = (selection: API.FileDTO[]) => {
  emit("selectionChange", selection);
};

/**
 * 设置表格行的类名
 * @returns 自定义行类名
 */
const tableRowClassName = () => {
  return "custom-row";
};
</script>

<style scoped>
el-table-text-color {
  color: #1f335d;
}

.file-name {
  display: flex;
  align-items: center;
  cursor: pointer;
}
.file-name:hover .file-name-text {
  text-decoration: underline;
  color: aqua;
}
.file-icon {
  width: 28px;
  height: 28px;
  margin-right: 14px;
}
.file-name-text {
  font-size: 18px;
  color: black;
}
:deep(.custom-row) {
  height: 54px;
}
:deep(.el-table__header) th {
  font-size: 15px;
  font-weight: bold;
  background-color: #f5f7fa;
  padding: 14px 0;
}
:deep(.el-table__body) td {
  padding: 14px 0;
  font-size: 14px;
}
:deep(.el-table--enable-row-hover .el-table__body tr:hover > td) {
  background-color: #c6d5ed;
}
:deep(.el-checkbox__inner) {
  width: 18px;
  height: 18px;
}
</style>
