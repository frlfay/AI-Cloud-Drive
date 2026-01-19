<template>
  <div class="login-container">
    <el-tabs v-model="activeTab" class="login-tabs">
      <el-tab-pane label="扫码登录" name="qr">
        <div class="qr-code">
          <!-- 二维码占位符 -->
          <div class="qr-placeholder">二维码占位符</div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="账号登录" name="account">
        <el-form ref="formRef" :model="loginForm" :rules="loginRules">
          <el-form-item prop="phone">
            <el-input v-model="loginForm.phone" placeholder="请输入用户名">
              <template #prefix>
                <el-icon>
                  <User />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password>
              <template #prefix>
                <el-icon>
                  <Lock />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="loading" class="submit-btn">
              登录
            </el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
    <div class="form-footer">
      <el-checkbox v-model="rememberMe">下次自动登录</el-checkbox>
      <el-link type="primary" @click="$router.push('/user/register')">
        还没有账号？注册一个
      </el-link>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useLoginUserStore } from "@/store/user";
import { Lock, User } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { reactive, ref } from "vue";
import type { FormInstance } from "element-plus";
import { useRouter } from "vue-router";


const loading = ref(false);
const activeTab = ref("account");
const rememberMe = ref(false);
const router = useRouter();

const { login } = useLoginUserStore();

const loginRules = {
  phone: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

const formRef = ref<FormInstance>();

/**
 * 登录表单
 */
const loginForm = reactive({
  phone: "",
  password: "",
} as API.UserLoginRequest);

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
  } catch (error) {
    ElMessage.error("参数校验失败");
    return;
  }

  loading.value = true;
    await login(loginForm);
    loading.value = false;
};
</script>

<style scoped>
.login-container {
  width: 100%;
  max-width: 360px;
}

.login-tabs {
  margin-bottom: 20px;
}

.qr-code {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}

.qr-placeholder {
  width: 180px;
  height: 180px;
  background-color: #f0f0f0;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  color: #999;
  border: 1px dashed #ccc;
}

.submit-btn {
  width: 100%;
  padding: 12px 0;
}

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

:deep(.el-input__wrapper) {
  box-shadow: none;
  border-bottom: 1px solid #dcdfe6;
}

:deep(.el-input__inner) {
  height: 40px;
}

:deep(.el-tabs__nav) {
  width: 100%;
}

:deep(.el-tabs__item) {
  width: 50%;
  text-align: center;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}
</style>
