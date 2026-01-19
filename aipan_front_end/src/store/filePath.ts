import { defineStore } from "pinia";
import { ref } from "vue";

export const useFileStores = defineStore(
  "filePaths", // 更改store的名字以更好地反映其用途
 
  () => {
    // 定义一个引用类型的数组，用来存储文件路径
    const filePaths = ref<string[]>([]);
    const filepath = ref("");

    // 添加一个方法用于向数组中添加新的文件路径
    function setFilePath(newFilename: string) {
      filepath.value = newFilename;
    }
    function clearFilePaths() {
      filePaths.value = [];
    }

    function addFilePath(newPath: string) {
      // 检查是否已经存在该路径，避免重复添加
      if (!filePaths.value.includes(newPath)) {
        filePaths.value.push(newPath);
      }
    }
    

    return {
      filePaths, // 返回数组以便外部访问
      addFilePath, // 返回方法以便外部调用
      filepath,
      setFilePath,
      clearFilePaths,
    };
  },
  { persist: true } // 保持持久化配置
);
