<template>
  <div class="capacity-process" :class="{ 'is-collapse': isCollapse }">
    <template v-if="isCollapse">
      <!-- 圆形进度条 -->
      <div class="circle-container">
        <el-tooltip
          :content="`已使用: ${formatStorage(
            usedStorage
          )}\n总容量: ${formatStorage(totalStorage)}`"
          placement="right"
        >
          <div class="progress-wrapper">
            <el-progress
              type="circle"
              :percentage="usagePercentage"
              :status="progressStatus"
              :width="36"
              :stroke-width="6"
              :show-text="false"
            />
            <span class="percentage-text">{{ usagePercentage }}%</span>
          </div>
        </el-tooltip>
        <div class="storage-info-mini">
          <span>{{ formatStorage(usedStorage) }}</span>
        </div>
      </div>
    </template>
    <template v-else>
      <!-- 条形进度条 -->
      <div class="storage-title">存储空间</div>
      <el-progress
        :percentage="usagePercentage"
        :status="progressStatus"
        :stroke-width="8"
        :show-text="false"
      />
      <div class="storage-info">
        <div class="used-info">
          <span class="label">已使用</span>
          <span class="value">{{ formatStorage(usedStorage) }}</span>
        </div>
        <div class="total-info">
          <span class="value">{{ formatStorage(totalStorage) }}</span>
          <span class="label">总容量</span>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed } from "vue";
import { useStorageStore } from "@/store/storage";

// 接收父组件传递的折叠状态
const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false,
  },
});

const storageStore = useStorageStore();

// 计算已使用存储空间
const usedStorage = computed(() => {
  return Number(storageStore.storageSize || 0);
});

// 计算总存储空间
const totalStorage = computed(() => {
  return Number(storageStore.totalStorageSize || 0);
});

// 计算使用百分比
const usagePercentage = computed(() => {
  if (!totalStorage.value) return 0;
  return Math.round((usedStorage.value / totalStorage.value) * 100);
});

// 根据使用比例决定进度条状态
const progressStatus = computed(() => {
  const percentage = usagePercentage.value;
  if (percentage >= 90) return "exception";
  if (percentage >= 70) return "warning";
  return "success";
});

// 格式化存储大小显示
const formatStorage = (bytes: number): string => {
  if (!bytes) return "0 B";
  const units = ["B", "KB", "MB", "GB", "TB"];
  let value = bytes;
  let unitIndex = 0;

  while (value >= 1024 && unitIndex < units.length - 1) {
    value /= 1024;
    unitIndex++;
  }

  return `${value.toFixed(2)} ${units[unitIndex]}`;
};

onMounted(() => {
  storageStore.fetchStorageInfo();
});
</script>

<style scoped>
.capacity-process {
  padding: 16px;
  background-color: var(--el-menu-bg-color);
  transition: all 0.3s;
}

.capacity-process.is-collapse {
  padding: 8px 4px;
}

.circle-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.progress-wrapper {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

.percentage-text {
  position: absolute;
  font-size: 9px;
  color: var(--el-text-color-regular);
  font-weight: normal;
}

.storage-info-mini {
  font-size: 8px;
  color: var(--el-text-color-secondary);
  line-height: 1;
  margin-top: 2px;
}

.storage-title {
  color: var(--el-text-color-secondary);
  font-size: 13px;
  margin-bottom: 12px;
}

.storage-info {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  font-size: 12px;
}

.used-info,
.total-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.label {
  color: var(--el-text-color-secondary);
}

.value {
  color: var(--el-text-color-primary);
  font-weight: 500;
}

:deep(.el-progress-bar__outer) {
  background-color: var(--el-fill-color-light);
  border-radius: 4px;
}

:deep(.el-progress-bar__inner) {
  border-radius: 4px;
  transition: all 0.3s;
}

:deep(.el-progress-circle) {
  transform: rotate(-90deg);
}

:deep(.el-progress-circle path:first-child) {
  opacity: 0.12;
  stroke: var(--el-border-color-darker);
}

:deep(.el-progress-circle path:last-child) {
  stroke-linecap: round;
  stroke-width: 6;
}

:deep(.el-progress.is-success path:last-child) {
  stroke: var(--el-color-success);
}

:deep(.el-progress.is-warning path:last-child) {
  stroke: var(--el-color-warning);
}

:deep(.el-progress.is-exception path:last-child) {
  stroke: var(--el-color-danger);
}
</style>
