<template>
  <div class="breadcrumb-wrapper">
    <div class="title">当前位置：</div>
    <div
      v-if="!isShowInput"
      class="breadcrumb-box"
      :class="{ 'able-input': isAllFiles }"
      
    >
      <el-breadcrumb separator=">">
        <el-breadcrumb-item v-if="!isAllFiles">{{
          currentFileTypeName
        }}</el-breadcrumb-item>
        <template v-else>
          <el-breadcrumb-item @click="handleRootClick"
            >全部文件夹</el-breadcrumb-item
          >
          <el-breadcrumb-item
            v-for="(item, index) in pathParts"
            :key="index"
            @click="handlePartClick(index)"
          >
            {{ item }}
          </el-breadcrumb-item>
        </template>
      </el-breadcrumb>
    </div>
  
  </div>
</template>


<script setup lang="ts">
import { ref, computed, nextTick, watch,onMounted } from "vue";
import { useFileStores } from "@/store/filePath";
import { useLoginUserStore } from "@/store/user";
const props = defineProps<{
  currentPath: string;
  fileType?: number;
}>();

const emit = defineEmits(["pathChange"]);

const isShowInput = ref(false);
const inputFilePath = ref(localStorage.getItem("Path"));
const filePathInputRef = ref<HTMLInputElement | null>(null);

const fileTypeMap: Record<number, string> = {
  1: "全部图片",
  2: "全部视频",
  3: "全部音频",
  4: "全部文档",
  5: "压缩文件",
  6: "其他",
};

const isAllFiles = computed(() => props.fileType === undefined);

const currentFileTypeName = computed(() => {
  return props.fileType !== undefined
    ? fileTypeMap[props.fileType]
    : "全部文件";
});

const pathParts = computed(() => {
  return props.currentPath.split("/").filter(Boolean);
});

// function handleClickBreadCrumbSelf(event: MouseEvent) {
//   if (isAllFiles.value && event.target === event.currentTarget) {
//     inputFilePath.value = props.currentPath;
//     isShowInput.value = true;
//     nextTick(() => {
//       filePathInputRef.value?.focus();
//     });
//   }
// }

onMounted(() => {
  
    // emit("pathChange", inputFilePath.value); // 触发父组件更新路径
  // updatePath();

  isShowInput.value = false;
  if (inputFilePath.value !== props.currentPath) {
    const newPath = inputFilePath.value?.endsWith("/")
      ? inputFilePath.value
      : `${inputFilePath.value}/`;
  
   
    emit("pathChange", newPath,localStorage.getItem("parent_id"));
  }

});

// function handleInputBlur() {
//   updatePath();
// }

// function handleInputEnter() {
//   updatePath();
// }

// function updatePath() {
//   isShowInput.value = false;
//   if (inputFilePath.value !== props.currentPath) {
//     const newPath = inputFilePath.value?.endsWith("/")
//       ? inputFilePath.value
//       : `${inputFilePath.value}/`;
  
   
//     emit("pathChange", newPath);
//   }
// }

function handleRootClick() {
  const {loginUser} = useLoginUserStore();
  emit("pathChange", "/",loginUser.rootFileId);
  
}

function handlePartClick(index: number) {
  const newPath = "/" + pathParts.value.slice(0, index + 1).join("/") + "/";
  const {filePaths} = useFileStores();
  emit("pathChange", newPath,filePaths[index+1]);
  console.log("333333333",index);
}

// 监听 fileType 的变化
watch(
  () => props.fileType,
  () => {
    isShowInput.value = false;
  }
);

// 监听 currentPath 的变化
watch(
  () => props.currentPath,
  (newPath) => {
    inputFilePath.value = newPath;
    localStorage.setItem("Path", inputFilePath.value);
  }
);

</script>

<style scoped>
.breadcrumb-wrapper {
  display: flex;
  align-items: center;
  height: 40px;
  padding: 0 16px;
  border-radius: 4px;
  position: relative;
  overflow: hidden;
  background: linear-gradient(45deg, #959595, #000000);
  background-size: 200% 200%;
  animation: gradientFlow 5s ease infinite;
  color: white /* 根据背景颜色调整文字颜色 */
}

@keyframes gradientFlow {
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

.title {
  margin-right: 8px;
  font-size: 15px;
  color: #f8f9fa;
}

.file-path-input {
  flex: 1;
  height: 28px;
  font-size: 15px;
}

.breadcrumb-box {
  flex: 1;
  display: flex;
  align-items: center;
}

.breadcrumb-box.able-input {
  cursor: pointer;
}

:deep(.el-breadcrumb__item) {
  cursor: pointer;
}

:deep(.el-breadcrumb__inner) {
  color: #409eff;
  font-size: 16px;
  padding: 6px 10px; /* 增加内边距 */
  border-radius: 4px; /* 添加圆角 */
  transition: background-color 0.3s; /* 添加过渡效果 */
}

:deep(.el-breadcrumb__inner:hover) {
  background-color: #e6e8eb; /* 悬浮时的背景色 */
}

:deep(.el-breadcrumb__separator) {
  margin: 0 5px; /* 减小分隔符间距 */
  font-size: 16px;
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #fdfdfd;
  cursor: default;
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner:hover) {
  background-color: transparent; /* 最后一项不显示悬浮效果 */
}
</style>
