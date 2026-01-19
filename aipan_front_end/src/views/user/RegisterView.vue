<template>
  <div class="register-container">
    <h2 class="form-title">注册账号</h2>
    <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef">
      <el-form-item prop="phone">
        <el-input v-model="registerForm.phone" placeholder="请输入用户名">
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="username">
        <el-input
          v-model="registerForm.username"
          type="password"
          placeholder="请设置登录密码"
          show-password
        >
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          placeholder="请确认登录密码"
          show-password
        >
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item label="头像">
          <div style="width: 120px">
            <ImageUpload v-model="avatarUrl"></ImageUpload>
          </div>
        </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          @click="handleRegister"
          :loading="loading"
          class="submit-btn"
        >
          注册
        </el-button>
      </el-form-item>
    </el-form>
    <div class="form-footer">
      <el-link type="primary" @click="$router.push('/user/login')">
        已有账号？直接登录
      </el-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive,watch } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { User, Lock } from "@element-plus/icons-vue";
import type { FormInstance, FormItemRule } from "element-plus";
import { userRegister } from "@/api/user";
import ImageUpload from "@/components/common/ImageUpload.vue";
import {useLoginUserStore} from "@/store/user"

const avatarUrl = ref("");
avatarUrl.value = avatarUrl.value.replace("http://localhost:8080/user/", "");
const router = useRouter();
const loading = ref(false);
const registerFormRef = ref<FormInstance | null>(null);


const registerForm = reactive({
  phone: "",
  username: "",
  password: "",
  avatarUrl: "",
} as API.UserRegisterRequest);

watch(avatarUrl, (newVal) => {
  registerForm.avatarUrl = newVal;
}, { immediate: true });
if (localStorage.getItem("avatarUrl")) {
  avatarUrl.value = localStorage.getItem("avatarUrl")!;
}

const registerRules = {
  phone: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  username: [
    { required: true, message: "请设置登录密码", trigger: "blur" },
  ],
  password: [
    {
      validator: (_rule: FormItemRule, value: string) => {
        return new Promise((resolve, reject) => {
          if (value !== registerForm.username) {
            reject(new Error("两次输入的密码不一致"));
          } else {
            resolve(void 0);
          }
        });
      },
      trigger: "blur",
    },
  ],
};

const handleRegister = async () => {
  if(!avatarUrl){
    ElMessage.error("请等待头像上传完成");
    return;}
  if (!registerFormRef.value) return;

  try {
    await registerFormRef.value.validate();
  } catch (error) {
    ElMessage.error("参数校验失败");
    return;
  }

  try {
    loading.value = true;
    if(registerForm.avatarUrl){
    const res = await userRegister(registerForm);
  
    if (res.data.code === 0 ) {
      
      ElMessage.success("注册成功，请登录");
      router.push("/user/login");
      localStorage.removeItem("avatarUrl");
    } else {
      ElMessage.error(`注册失败：${res.data.msg}`);
    }
  }else{
    ElMessage.error("请上传头像");
  }
 } catch (error) {
    ElMessage.error("注册过程中发生未知错误");
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.register-container {
  width: 100%;
  max-width: 360px;
}

.form-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

.submit-btn {
  width: 100%;
  padding: 12px 0;
}

.form-footer {
  margin-top: 20px;
  text-align: center;
}

:deep(.el-input__wrapper) {
  box-shadow: none;
  border-bottom: 1px solid #dcdfe6;
}

:deep(.el-input__inner) {
  height: 40px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}
</style>
