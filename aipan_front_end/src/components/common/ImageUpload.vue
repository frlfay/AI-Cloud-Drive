<template>
  <el-upload
    class="avatar-uploader"
    :show-file-list="false"
    :before-upload="beforeAvatarUpload"
    :http-request="customUpload"
  >
    <img v-if="imageUrl" :src="imageUrl" class="avatar" />
    <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
  </el-upload>
</template>

<script setup lang="ts">
import { uploadAvatar } from "@/api/user";
import { Plus } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { defineEmits, defineProps, ref, watch } from "vue";
// import {useLoginUserStore} from "@/store/user"

interface Props {
  modelValue?: string; // 图片URL，支持v-model双向绑定
}

const props = defineProps<Props>();
const emit = defineEmits(["update:modelValue"]);

// 初始化时清理URL，移除不必要的前缀
const initialImageUrl = props.modelValue?.replace("http://localhost:8080/user/", "") || "";
const imageUrl = ref(initialImageUrl);

watch(
  () => props.modelValue,
  (newValue) => {
    // 监听modelValue变化，同步更新本地图片URL并清理
    imageUrl.value = newValue?.replace("http://localhost:8080/user/", "") || "";
  }
);

/**
 * 上传前的文件验证
 * 验证文件格式是否为JPG或PNG，且大小不超过2MB
 * @param file 待上传的文件对象
 * @returns boolean 是否通过验证
 */
const beforeAvatarUpload = (file: File) => {
  const isJPG = file.type === "image/jpeg";
  const isPNG = file.type === "image/png";
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG && !isPNG) {
    ElMessage.error("上传头像图片只能是 JPG 或 PNG 格式!");
    return false;
  }
  if (!isLt2M) {
    ElMessage.error("上传头像图片大小不能超过 2MB!");
    return false;
  }
  return true;
};

/**
 * 自定义上传实现
 * 处理文件上传逻辑，成功后更新图片URL并通知父组件
 * @param options 上传选项，包含文件对象和回调函数
 */
const customUpload = async (options: any) => {
  try {
    const formData = new FormData();
    formData.append("file", options.file);
    const res = await uploadAvatar(formData);

    if (
      res &&
      res.data &&
      res.data.code === 0 &&
      typeof res.data.data === "string"
    ) {
      // 清理URL，移除不必要的前缀，并确保是绝对路径
      let avatarUrl = res.data.data;
        avatarUrl = `http://${avatarUrl}`;
      imageUrl.value = avatarUrl;
      localStorage.setItem("avatarUrl", avatarUrl);
      emit("update:modelValue", avatarUrl);
      console.log("0000000000",avatarUrl);
      // useLoginUserStore().setAvatarUrl(avatarUrl);
      ElMessage.success("头像上传成功");
      options.onSuccess();
    } else {
      throw new Error(res?.data?.msg || "上传失败");
    }
  } catch (error) {
    ElMessage.error("上传失败，请稍后重试");
    options.onError(error);
  }
};
</script>

<style scoped>
.avatar-uploader {
  text-align: center;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  aspect-ratio: 1 / 1;
  width: 100%;
  max-width: 178px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}

.avatar {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}
</style>