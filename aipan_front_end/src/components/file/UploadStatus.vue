<template>
  <div class="upload-status">
    <div v-for="file in files" :key="file.uid" class="file-item">
      <el-icon class="file-icon"><document /></el-icon>
      <div class="file-info">
        <div class="file-name">{{ file.name }}</div>
        <div class="file-size">{{ formatFileSize(file.size) }}</div>
      </div>
      <el-progress
        :percentage="file.percentage || 0"
        :status="getProgressStatus(file.status)"
      />
      <div class="file-speed" v-if="file.speed">
        {{ formatSpeed(file.speed) }}
      </div>
      <div class="file-status">{{ getStatusText(file.status) }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { Document } from "@element-plus/icons-vue";

const props = defineProps({
  files: {
    type: Array,
    required: true,
  },
});

/**
 * 格式化文件大小显示
 * 将字节大小转换为人类可读的格式（B、KB、MB、GB）
 * @param {number} size - 文件大小（字节）
 * @returns {string} 格式化后的文件大小字符串
 */
const formatFileSize = (size) => {
  if (size < 1024) return size + " B";
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + " KB";
  if (size < 1024 * 1024 * 1024)
    return (size / (1024 * 1024)).toFixed(2) + " MB";
  return (size / (1024 * 1024 * 1024)).toFixed(2) + " GB";
};

/**
 * 格式化上传速度显示
 * @param {number} speed - 上传速度（字节/秒）
 * @returns {string} 格式化后的速度字符串
 */
const formatSpeed = (speed) => {
  if (speed < 1024) return speed.toFixed(2) + " B/s";
  if (speed < 1024 * 1024) return (speed / 1024).toFixed(2) + " KB/s";
  return (speed / (1024 * 1024)).toFixed(2) + " MB/s";
};

/**
 * 获取进度条状态
 * @param {string} status - 文件上传状态
 * @returns {string} 进度条状态类型
 */
const getProgressStatus = (status) => {
  switch (status) {
    case "success":
      return "success";
    case "error":
      return "exception";
    default:
      return "";
  }
};

/**
 * 获取上传状态文本
 * @param {string} status - 文件上传状态
 * @returns {string} 状态对应的中文文本
 */
const getStatusText = (status) => {
  switch (status) {
    case "ready":
      return "准备上传";
    case "uploading":
      return "上传中";
    case "success":
      return "上传成功";
    case "error":
      return "上传失败";
    default:
      return "未知状态";
  }
};
</script>

<style scoped>
.upload-status {
  margin-top: 20px;
}

.file-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  padding: 10px;
  border-radius: 4px;
  background-color: #f5f7fa;
}

.file-icon {
  font-size: 24px;
  margin-right: 10px;
  color: #909399;
}

.file-info {
  flex-grow: 1;
  margin-right: 10px;
}

.file-name {
  font-size: 14px;
  color: #303133;
}

.file-size {
  font-size: 12px;
  color: #909399;
}

.file-speed {
  font-size: 12px;
  color: #409eff;
  min-width: 80px;
  text-align: right;
  margin-right: 10px;
}

.file-status {
  font-size: 12px;
  color: #67c23a;
  min-width: 60px;
  text-align: right;
}

.el-progress {
  width: 200px;
  margin-right: 10px;
}
</style>
