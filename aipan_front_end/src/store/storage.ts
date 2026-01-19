import { defineStore } from "pinia";
import { ref } from "vue";
import { currentUser } from "@/api/user";

export const useStorageStore = defineStore("storage", () => {
  const storageSize = ref<string>("0");
  const totalStorageSize = ref<string>("0");

  // 获取存储信息
  const fetchStorageInfo = async () => {
    try {
      const res = await currentUser();
      if (res.data?.code === 0 && res.data.data) {
        console.log("存储信息:",res.data.data)
        const { usedSize, totalSize } = res.data.data.storageDTO;
        storageSize.value = String(usedSize);
        totalStorageSize.value = String(totalSize);
        console.log("获取存储信息成功:", res.data.data.storageDTO);
      }
    } catch (error) {
      console.error("获取存储信息失败:", error);
    }
  };

  // 更新存储信息
  const updateStorageInfo = () => {
    fetchStorageInfo();
  };

  return {
    storageSize,
    totalStorageSize,
    fetchStorageInfo,
    updateStorageInfo,
  };
});
