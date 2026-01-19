import router from "@/router";
import { useLoginUserStore } from "@/store/user";
import { ElMessage } from "element-plus";

router.beforeEach(async (to, from, next) => {
  const { loginStatus } = useLoginUserStore();
  if (to.meta.isAuth) {
    if (loginStatus) {
      next();
    } else {
      next('/user/login')
      ElMessage.error("请先登录！");
    }
  } else {
    next();
  }
});
