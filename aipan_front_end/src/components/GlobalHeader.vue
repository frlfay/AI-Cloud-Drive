<template>
  <div class="global-header">
    <div class="left">
      <img src="@/assets/logo.svg" alt="Logo" class="logo" />
      <h1 class="title">小滴云盘</h1>
      <el-menu mode="horizontal" :ellipsis="false" class="menu">
        <el-menu-item v-for="item in visibleRoutes" :key="item.path" :index="item.path" :active="selectedKeys"
          @click="doMenuClick(item.path)">
          {{ item.name }}
        </el-menu-item>
      </el-menu>
    </div>
    <div class="right">
      <template v-if="loginStatus">
        <el-dropdown trigger="hover">
          <div class="user-info">
            <el-avatar :size="38" :src="loginUser.avatarUrl ||
              getDefaultAvatar(loginUser.id)
              " />
            <span class="username">
              {{ loginUser.username }}
            </span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="openSettingsDialog">
                个人设置
              </el-dropdown-item>
              <el-dropdown-item @click="handleLogout">
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
      <template v-else>
        <el-button type="primary" @click="doMenuClick('/user/login')">
          登录
        </el-button>
      </template>
    </div>
  </div>
  <el-dialog title="个人设置" v-model="settingsDialogVisible" width="500px">
    <el-form :model="userSettingsForm" label-width="100px">
      <el-form-item label="用户名">
        <el-input v-model="userSettingsForm.username" />
      </el-form-item>
      <el-form-item label="头像">
        <div style="width: 120px">
          <ImageUpload v-model="userSettingsForm.avatarUrl" />
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="settingsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateUserSettings">保存</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import checkAccess from "@/access/checkAccess";
import { updateUserInfo } from "@/api/user";
import ImageUpload from "@/components/common/ImageUpload.vue";
import { useLoginUserStore } from "@/store/user";
import { ElMessage } from "element-plus";
import { computed, ref, watch } from "vue";
import { useRouter } from "vue-router";
import { routes } from "../router/routes";

const router = useRouter();
const { loginStatus, logout, loginUser, fetchLoginUser } = useLoginUserStore();

/**
 * 处理菜单点击事件，进行路由跳转
 * @param key 目标路由路径
 */
const doMenuClick = (key: string) => {
  router.push({
    path: key,
  });
};

/**
 * 处理退出登录
 * 清除用户登录状态并跳转到登录页
 */
const handleLogout = async () => {
  await logout();
  router.push("/user/login");
};

// 当前选中的菜单项
const selectedKeys = ref(["/"]);

// 路由跳转后，更新选中的菜单项
router.afterEach((to, from, failure) => {
  selectedKeys.value = [to.path];
});

/**
 * 根据权限过滤并计算可见的路由菜单
 * 过滤掉隐藏菜单和无权限访问的菜单
 */
const visibleRoutes = computed(() => {
  return routes.filter((item) => {
    if (item.meta?.hideInMenu) {
      return false;
    }
    if (!checkAccess(loginUser, item.meta?.access as number)) {
      return false;
    }
    return true;
  });
});

const settingsDialogVisible = ref(false);
const userSettingsForm = ref<API.UserUpdateMyRequest>({});

/**
 * 打开个人设置对话框
 * 初始化表单数据为当前用户信息
 */
const openSettingsDialog = () => {
  userSettingsForm.value = {
    userId: useLoginUserStore().loginUser.id,
    username: useLoginUserStore().loginUser.username,
    avatarUrl: useLoginUserStore().loginUser.avatarUrl,
    // email: loginUser.email,
  };
  settingsDialogVisible.value = true;
};

/**
 * 更新用户设置
 * 提交更新请求并处理响应
 */
const updateUserSettings = async () => {
  try {
    const res = await updateUserInfo({}, userSettingsForm.value);
    if (res.data.code === 0) {
      ElMessage.success("个人信息更新成功");
      await useLoginUserStore().fetchLoginUser();
      loginUser.avatarUrl = useLoginUserStore().loginUser.avatarUrl;
      settingsDialogVisible.value = false;
     
      
    } else {
      ElMessage.error("更新失败，" + res.data?.msg);
    }
  } catch (error) {
    ElMessage.error("更新失败，请稍后重试");
  }
};

watch(
  () => useLoginUserStore().loginUser.username,
  (newValue) => {
    if (newValue) {
      userSettingsForm.value.username = newValue;
      loginUser.username = newValue;
    }
  }
);
/**
 * 获取默认头像URL
 * 根据用户ID或随机数生成头像
 * @param userId 用户ID
 * @returns 头像URL
 */
const getDefaultAvatar = (userId?: number) => {
  const seed =
    userId?.toString() || Math.random().toString(36).substring(2, 15);
  return `https://api.dicebear.com/9.x/micah/svg?seed=${seed}`;
};
</script>

<style scoped>
.global-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.left {
  display: flex;
  align-items: center;
}

.logo {
  width: 50px;
  height: 50px;
  margin-right: 10px;
}

.title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-right: 20px;
}

@media screen and (max-width: 768px) {
  .title {
    display: none;
  }

  .logo {
    margin-right: 5px;
  }
}

.menu {
  border-bottom: none;
}

.right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 12px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  margin-left: 10px;
  font-size: 16px;
  color: rgba(128, 128, 128, 0.65);
  font-weight: 500;
}

:deep(.el-dropdown) {
  color: inherit;
}

:deep(.el-dropdown:focus-visible) {
  outline: none;
}

:deep(.el-popper) {
  border-radius: 8px;
}

:deep(.el-dropdown-menu__item) {
  padding: 8px 20px;
}
</style>
