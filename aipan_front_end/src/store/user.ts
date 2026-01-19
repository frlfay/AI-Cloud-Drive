import { defineStore } from "pinia";
import { ref } from "vue";
import { currentUser, userLogin } from "@/api/user";
import { ElMessage } from "element-plus";
import { useFileStores } from "@/store/filePath";
import { useRouter } from "vue-router";

export const useLoginUserStore = defineStore("loginUser", () => {
  const router = useRouter();
  // 登录状态
  const loginStatus = ref(false);

  // 用户token
  const token = ref("");

  const avatarUrl = ref("");

  function setAvatarUrl(newAvatarUrl: string) {
    avatarUrl.value = newAvatarUrl;
  }

  //用户信息
  const loginUser = ref<API.UserDTO>({
    /** 用户ID */
    id: 0,
    
    /* 根文件夹名  */
    rootFileName: '',
    
    /* 根文件ID  */
    rootFileId: '',

    /** 用户名 */
    username: '',

    /** 用户头像URL */
    avatarUrl: '',

    /** 用户邮箱 */
    email: '',

    /** 用户容量大小 */
    capacity: 0,

    /** 用户存储信息 */
    storageDTO: {
      accountId: '',
      id: '',
      totalSize: '',
      usedSize: '',
    },

    /** 用户角色 */
    role: 0,

    /** 创建时间 */
    createTime: '',

    /** 更新时间 */
    updateTime: '',
  });

  // 登录
  async function login(loginForm: any): Promise<any> {
    const res = await userLogin(loginForm);
    if (res.data.code === 0) {
      loginStatus.value = true;
      token.value = res.data.data;
      await fetchLoginUser();
      router.push({ path: "/", replace: true });
      ElMessage.success("登录成功");
      useFileStores().clearFilePaths();
      localStorage.setItem("Path", "/");
      localStorage.setItem("parent_id",loginUser.value.rootFileId!);
    } else {
      ElMessage.error(`登录失败：账号或密码错误`);
      return;
    }
  }

  // 更新用户信息
  async function fetchLoginUser() {
    const res = await currentUser();
    console.log("res:",res);
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data;
      console.log("刷新用户信息:",res.data.data);
    }
  }

  // 退出登录
  async function logout() {
    loginStatus.value = false;
    token.value = "";
    loginUser.value = {
      id: 0,
      username: '',
      avatarUrl: '',
      email: '',
      capacity: 0,
      storageDTO: {
        accountId: '',
        id: '',
        totalSize: '',
        usedSize: '',
      },
      role: 0,
      createTime: '',
      updateTime: '',
    };
    useFileStores().filePaths = [];
    ElMessage.success("退出登录成功");
  }

  return { loginUser, login, fetchLoginUser, logout, loginStatus, token, avatarUrl, setAvatarUrl };
},
  { persist: true }
);
