<template>
  <el-dialog v-model="dialogVisible" title="文件详情" width="40%">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="文件名">
        {{ fileInfo.fileName }}
      </el-descriptions-item>
      <el-descriptions-item label="文件类型">
        {{
          fileInfo.isDirectory === 1
            ? "文件夹"
            : fileInfo.fileType?.toUpperCase()
        }}
      </el-descriptions-item>
      <el-descriptions-item label="文件大小">
        {{ formatFileSize(fileInfo.fileSize) }}
      </el-descriptions-item>
      <el-descriptions-item label="文件路径">
        {{ fileInfo.filePath }}
      </el-descriptions-item>
      <el-descriptions-item label="修改时间">
        {{ formatDateTime(fileInfo.updateTime) }}
      </el-descriptions-item>
    </el-descriptions>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed } from "vue";

const props = defineProps<{
  modelValue: boolean; // 控制对话框的显示/隐藏
  fileInfo: API.FileDTO; // 需要显示的文件信息对象
}>();

const emit = defineEmits(["update:modelValue"]);

/**
 * 双向绑定对话框的可见状态
 * 通过计算属性实现 v-model 的功能
 */
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit("update:modelValue", value),
});

/**
 * 格式化文件大小
 * 将字节数转换为合适的单位显示（B、KB、MB、GB、TB）
 * @param size 文件大小（字节）
 * @returns 格式化后的文件大小字符串
 */
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

/**
 * 格式化日期时间
 * 将时间戳转换为本地时间字符串
 * @param timestamp ISO格式的时间字符串
 * @returns 格式化后的日期时间字符串
 */
const formatDateTime = (timestamp: string | undefined) => {
  if (!timestamp) return "-";
  return new Date(timestamp).toLocaleString();
};
</script>

<style scoped>
:deep(.el-descriptions__label) {
  width: 120px;
  font-weight: bold;
}

:deep(.el-descriptions__content) {
  word-break: break-all;
}
</style>
