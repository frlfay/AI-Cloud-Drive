<template>
  <div class="file-grid">
    <el-row :gutter="20">
      <el-col
        v-for="file in fileList"
        :key="file.id"
        :xs="12"
        :sm="8"
        :md="6"
        :lg="4"
        :xl="3"
      >
        <div
          class="file-item"
          :class="{ 'selection-mode': isSelectionMode }"
          @click="handleFileClick(file)"
        >
          <div v-if="isSelectionMode" class="file-checkbox">
            <el-checkbox v-model="selectedFileIds" :label="file.id" @click.stop>
              <!-- 空的标签内容，不显示任何文本 -->
            </el-checkbox>
          </div>
          <div class="file-icon-wrapper">
            <el-image
              :src="getFileIcon(file.fileSuffix ?? '')"
              fit="contain"
              class="file-icon"
            />
          </div>
          <div class="file-name">{{ file.fileName }}</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { getFileIcon } from "@/utils/fileIcons";
import type { FileDTO } from "@/api/types"; // 假设您有定义API.FileDTO类型

const props = defineProps<{
  fileList: FileDTO[]; // 文件列表数据
  isSelectionMode: boolean; // 是否处于多选模式
}>();

const emit = defineEmits(["fileClick", "selectionChange"]);

// 存储被选中的文件ID数组
const selectedFileIds = ref<number[]>([]);

/**
 * 处理文件点击事件
 * 仅在非选择模式下触发文件点击事件
 * @param file 被点击的文件对象
 */
const handleFileClick = (file: FileDTO) => {
  if (!props.isSelectionMode) {
    emit("fileClick", file);
  } else {
    // 在选择模式下，切换文件的选中状态
    const index = selectedFileIds.value.indexOf(file.id);
    if (index > -1) {
      selectedFileIds.value.splice(index, 1);
    } else {
      selectedFileIds.value.push(file.id);
    }
    updateSelection();
  }
};

/**
 * 更新选中的文件列表
 * 根据选中的文件ID过滤出完整的文件对象列表，并触发选择变更事件
 */
const updateSelection = () => {
  // 过滤出已被选中的文件
  const selectedFileObjects = props.fileList.filter((file) =>
    selectedFileIds.value.includes(file.id)
  );
  console.log("已选文件：", selectedFileObjects);
  emit("selectionChange", selectedFileObjects);
};

// 监听选中文件变化，触发选择更新
watch(
  selectedFileIds,
  () => {
    updateSelection();
  },
  { immediate: true }
);

/**
 * 监听选择模式变化
 * 当退出选择模式时，清空所有选中状态
 */
watch(
  () => props.isSelectionMode,
  (newMode) => {
    if (!newMode) {
      selectedFileIds.value = [];
      updateSelection();
    }
  }
);
</script>

<style scoped>
.file-grid {
  margin-top: 20px;
  width: 100%;
  overflow-x: hidden;
}

.file-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  cursor: pointer;
  transition: all 0.3s;
  padding: 10px;
  position: relative;
  box-sizing: border-box;
}

.file-item:hover {
  background-color: #f5f7fa;
}

.file-item.selection-mode {
  cursor: default;
}

.file-checkbox {
  position: absolute;
  top: 5px;
  left: 5px;
  z-index: 1;
}

.file-checkbox :deep(.el-checkbox__label) {
  display: none;
}

.file-icon-wrapper {
  width: 80px;
  height: 80px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 8px;
}

.file-icon {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.file-name {
  width: 100%;
  text-align: center;
  word-break: break-all;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  font-size: 12px;
  color: #606266;
}

@media (max-width: 768px) {
  .file-icon-wrapper {
    width: 60px;
    height: 60px;
  }
}
</style>