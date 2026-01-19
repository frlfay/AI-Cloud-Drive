<template>
  <teleport to="body">
    <div v-show="visible" class="context-menu" :style="style" @click.stop>
      <div class="context-menu-item" @click="handleAction('view')">
        <el-icon><View /></el-icon>
        <span>查看</span>
      </div>
      <div class="context-menu-item" @click="handleAction('move')">
        <el-icon><Position /></el-icon>
        <span>移动</span>
      </div>
      <div class="context-menu-item" @click="handleAction('share')">
        <el-icon><Share /></el-icon>
        <span>分享</span>
      </div>
      <div class="context-menu-item" @click="handleAction('rename')">
        <el-icon><Edit /></el-icon>
        <span>重命名</span>
      </div>
      <div class="context-menu-item" @click="handleAction('copy')">
        <el-icon><DocumentCopy /></el-icon>
        <span>复制</span>
      </div>
      <div class="context-menu-item" @click="handleAction('info')">
        <el-icon><InfoFilled /></el-icon>
        <span>详情</span>
      </div>
      <div class="context-menu-divider"></div>
      <div class="context-menu-item delete" @click="handleAction('delete')">
        <el-icon><Delete /></el-icon>
        <span>删除</span>
      </div>
    </div>
  </teleport>
</template>

<script setup lang="ts">
import {
  View,
  Position,
  Share,
  Edit,
  InfoFilled,
  DocumentCopy,
  Delete,
} from "@element-plus/icons-vue";
import { computed, onMounted, onUnmounted } from "vue";

const props = defineProps<{
  visible: boolean; // 控制右键菜单的显示/隐藏
  x: number; // 右键菜单显示的 X 坐标位置
  y: number; // 右键菜单显示的 Y 坐标位置
}>();

const emit = defineEmits(["update:visible", "action"]);

/**
 * 计算右键菜单的定位样式
 * @returns 包含位置信息的样式对象
 */
const style = computed(() => ({
  position: "fixed" as const,
  top: `${props.y}px`,
  left: `${props.x}px`,
}));

/**
 * 处理右键菜单项点击事件
 * @param action 操作类型，可选值：'view'|'move'|'share'|'rename'|'info'|'delete'
 */
const handleAction = (action: string) => {
  emit("action", action);
  emit("update:visible", false);
};

/**
 * 关闭右键菜单
 * 当用户在菜单外部点击时触发
 */
const closeContextMenu = () => {
  emit("update:visible", false);
};

/**
 * 组件挂载时，添加全局点击事件监听器
 * 用于实现点击外部关闭菜单的功能
 */
onMounted(() => {
  document.addEventListener("click", closeContextMenu);
});

/**
 * 组件卸载时，移除全局点击事件监听器
 * 防止内存泄漏
 */
onUnmounted(() => {
  document.removeEventListener("click", closeContextMenu);
});
</script>

<style scoped>
.context-menu {
  position: fixed;
  background: white;
  border-radius: 4px;
  padding: 5px 0;
  min-width: 150px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 3000;
}

.context-menu-item {
  padding: 8px 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: background-color 0.3s;
}

.context-menu-item:hover {
  background-color: #f5f7fa;
}

.context-menu-item .el-icon {
  margin-right: 8px;
  font-size: 16px;
}

.context-menu-item span {
  font-size: 14px;
}

.context-menu-divider {
  margin: 4px 0;
  height: 1px;
  background-color: #ebeef5;
}

.context-menu-item.delete {
  color: #f56c6c;
}

.context-menu-item.delete:hover {
  background-color: #fef0f0;
}
</style>
