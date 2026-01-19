<template>
  <div id="basic-layout" class="flex flex-col h-screen">
    <el-header class="header h-15 p-0 m-0">
      <GlobalHeader />
    </el-header>
    <el-container class="flex-1 overflow-hidden">
      <template v-if="isFilePage">
        <el-aside :width="isCollapse ? '64px' : '200px'" class="aside">
          <AsideMenu :is-collapse="isCollapse" @select="handleFileTypeSelect" />
        </el-aside>
        <div class="aside-toggle">
          <el-tooltip
            effect="dark"
            :content="isCollapse ? '展开' : '收起'"
            placement="right"
          >
            <div class="aside-title" @click="toggleCollapse">
              <el-icon :size="12" class="toggle-icon">
                <component
                  :is="icons[isCollapse ? 'DArrowRight' : 'DArrowLeft']"
                />
              </el-icon>
            </div>
          </el-tooltip>
        </div>
      </template>
      <el-main class="main overflow-hidden p-0 flex-1">
        <router-view :fileType="fileType" />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import GlobalHeader from "@/components/GlobalHeader.vue";
import AsideMenu from "@/components/file/AsideMenu.vue";
import { DArrowLeft, DArrowRight } from "@element-plus/icons-vue";
import { computed, onMounted, ref } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const fileType = ref<number | null>(null);
const isCollapse = ref(false);

/**
 * 判断当前是否为文件相关页面
 * 包括文件列表、分享和回收站页面
 */
const isFilePage = computed(() => {
  const path = route.path;
  return path.startsWith("/file") || path === "/share" || path === "/recycle"|| path === "/Search"|| path === "/Answer"|| path === "/Grow" || path === "/Chat" || path === "/Document";
});

/**
 * 处理文件类型选择
 * 更新当前选中的文件类型过滤器
 * @param type 文件类型ID
 */
const handleFileTypeSelect = (type: number | null) => {
  fileType.value = type;
};

/**
 * 切换侧边栏的展开/收起状态
 * 并将状态保存到localStorage中
 */
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
  localStorage.setItem("asideMenuCollapsed", isCollapse.value.toString());
};

/**
 * 组件挂载时从localStorage读取侧边栏状态
 */
onMounted(() => {
  isCollapse.value = localStorage.getItem("asideMenuCollapsed") === "true";
});

const icons = {
  DArrowLeft,
  DArrowRight,
};
</script>

<style scoped>
#basic-layout {
  height: 100vh;
  margin: 0;
  padding: 0;
}

.header {
  height: 60px;
  padding: 0;
  margin: 0;
}

.el-container {
  height: calc(100vh - 60px);
}

.aside {
  border-right: 1px solid #e6e6e6;
  background-color: #fff;
  transition: width 0.3s;
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;
}

.main {
  background-color: #fff;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.aside-toggle {
  position: relative;
}

.aside-title {
  position: absolute;
  top: calc(50vh - 80px);
  z-index: 2;
  background: #ccc;
  width: 12px;
  height: 100px;
  cursor: pointer;
  border-radius: 0 8px 8px 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.aside-title:hover {
  background-color: #409eff;
}

.toggle-icon {
  color: white;
}
</style>
