<template>
  <div class="aside-container">
    <el-menu
      :default-active="activeIndex"
      class="aside-menu"
      :collapse="isCollapse"
      @select="handleSelect"
      router
    >
    <el-menu-item index="/file">
        <el-icon><FolderOpened /></el-icon>
        <template #title>我的文件</template>
      </el-menu-item>

      <el-menu-item index="/share">
        <el-icon><Share /></el-icon>
        <template #title>我的分享</template>
      </el-menu-item>

      <el-menu-item index="/recycle">
        <el-icon><Delete /></el-icon>
        <template #title>回收站</template>
      </el-menu-item>

      <el-sub-menu index="my-AI">
        <template #title>
          <el-icon><Menu /></el-icon>
          <span>人工智能</span>
        </template>

        <el-menu-item index="/Answer">
          <el-icon><Document /></el-icon>
          <template #title>AI网盘智答</template>
        </el-menu-item>
        <!-- <el-menu-item index="/Grow">
          <el-icon><HelpFilled /></el-icon>
          <template #title>AIGC文案智能体</template>
        </el-menu-item> -->
        <el-menu-item index="/Chat">
          <el-icon><Camera /></el-icon>
          <template #title>AI聊天智能助理</template>
        </el-menu-item>
        <el-menu-item index="/Document">
          <el-icon><Document /></el-icon>
          <template #title>Ai在线文档助手</template>
        </el-menu-item>
      </el-sub-menu>
    </el-menu>

    <!-- 添加容量进度条组件 -->
    <div class="capacity-container">
      <CapacityProcess :is-collapse="isCollapse" />
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  Delete,
  Document,
  Files,
  Folder,
  FolderOpened,
  Headset,
  More,
  Picture,
  Share,
  VideoPlay,
  Menu,
  Search,
  Camera,
  HelpFilled,
} from "@element-plus/icons-vue";
import { ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import CapacityProcess from "@/components/storage/CapacityProcess.vue";

const props = defineProps<{
  isCollapse: boolean; // 控制菜单是否折叠
}>();

const emit = defineEmits(["select"]);
const route = useRoute();
const router = useRouter();
const activeIndex = ref("");

/**
 * 处理菜单项选择事件
 * 根据选择的菜单项进行相应的路由跳转或文件类型筛选
 * @param index 选中菜单项的索引
 */
const handleSelect = (index: string) => {
  activeIndex.value = index;

  // 处理文件类型选择
  if (index === "0" || !isNaN(Number(index))) {
    emit("select", index === "0" ? null : Number(index));
    router.push("/file");
    return;
  }

  // 处理页面跳转
  if (index.startsWith("/")) {
    router.push(index);
  }
};

/**
 * 监听路由变化
 * 根据当前路由路径更新菜单选中状态
 */
watch(
  () => route.path,
  (newPath) => {
    if (newPath === "/file") {
      activeIndex.value = "0";
    } else {
      activeIndex.value = newPath;
    }
  },
  { immediate: true }
);
</script>

<style scoped>
.aside-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: var(--el-menu-bg-color);
}

.aside-menu {
  flex: 1;
  border-right: none;
}

:deep(.el-sub-menu .el-menu-item) {
  min-width: auto;
  padding-left: 40px !important;
}

:deep(.el-menu-item) {
  padding-left: 20px !important;
}

:deep(.el-sub-menu__title) {
  padding-left: 20px !important;
}
</style>
