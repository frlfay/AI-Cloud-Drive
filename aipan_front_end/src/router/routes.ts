import ACCESS_ENUM from "@/access/accessEnum";
import UserLayout from "@/layouts/UserLayout.vue";
import AboutView from "@/views/AboutView.vue";
import HomeView from "@/views/HomeView.vue";
import AdminUserView from "@/views/admin/AdminUserView.vue";
import FileView from "@/views/file/FileView.vue";
import MyShareView from "@/views/file/MyShareView.vue";
import RecycleView from "@/views/file/RecycleView.vue";
import LoginView from "@/views/user/LoginView.vue";
import RegisterView from "@/views/user/RegisterView.vue";
import NoAuthView from "@/views/error/NoAuthView.vue";
import NotFoundView from "@/views/error/NotFoundView.vue";
import ServerErrorView from "@/views/error/ServerErrorView.vue";
import { RouteRecordRaw } from "vue-router";
import SearchView from '@/views/file/Search.vue';
import PictureView from '@/views/file/Picture.vue';
import AIGCView from '@/views/file/AIGC.vue';
import ChatView from '@/views/file/Chat.vue';
import DocumentView from '@/views/file/Document.vue';
import AnswerView from '@/views/file/Answer.vue';


export const routes: Array<RouteRecordRaw> = [
  {
    path: "/user",
    name: "用户",
    component: UserLayout,
    children: [
      {
        path: "/user/login",
        name: "用户登录",
        component: LoginView,
      },
      {
        path: "/user/register",
        name: "用户注册",
        component: RegisterView,
      },
    ],
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/",
    name: "主页",
    component: HomeView,
    meta: {
      access: ACCESS_ENUM.NOT_LOGIN,
    },
  },
  {
    path: "/file",
    name: "文件",
    component: FileView,
    meta: { isAuth: true }, // 需要登录
  },
  {
    path: "/admin/user",
    name: "管理",
    component: AdminUserView,
    meta: {
      access: ACCESS_ENUM.ADMIN,
      isAuth: true
    },
  },
  {
    path: "/about",
    name: "关于",
    component: AboutView,
    meta: {
      access: ACCESS_ENUM.NOT_LOGIN,
      isAuth: true
    },
  },
  {
    path: "/share",
    name: "我的分享",
    component: MyShareView,
    meta: {
      hideInMenu: true,
      isAuth: true
    },
  },
  {
    path: "/recycle",
    name: "回收站",
    component: RecycleView,
    meta: {
      hideInMenu: true,
      isAuth: true
    },
  },
  {
    path: "/AI",
    name: "人工智能",
    component: MyShareView,
    meta: {
      hideInMenu: true,
      isAuth: true
    },
  },

  {
    path: "/Answer",
    name: "AI网盘智答",
    component: AnswerView,
    meta: {
      hideInMenu: true,
      isAuth: true
    },
  },
  {
    path: "/Grow",
    name: "AICG文案智能体",
    component: AIGCView,
    meta: {
      hideInMenu: true,
      isAuth: true
    },
  },
  {
    path: "/Chat",
    name: "AI聊天智能助理-可联网",
    component: ChatView,
    meta: {
      hideInMenu: true,
      isAuth: true
    },
  },
  {
    path: "/Document",
    name: "AI在线文档助手",
    component: DocumentView,
    meta: {
      hideInMenu: true,
      isAuth: true
    },
  },
  {
    path: "/share/:shareId",
    name: "分享",
    component: () => import("@/views/file/ShareView.vue"),
    meta: {
      hideInMenu: true,
      access: ACCESS_ENUM.NOT_LOGIN,
      isAuth: true
    },
  },
  // {
  //   path: "/403",
  //   name: "403",
  //   component: NoAuthView,
  //   meta: {
  //     hideInMenu: true,
  //   },
  // },
  // {
  //   path: "/404",
  //   name: "404",
  //   component: NotFoundView,
  //   meta: {
  //     hideInMenu: true,
  //   },
  // },
  // {
  //   path: "/500",
  //   name: "500",
  //   component: ServerErrorView,
  //   meta: {
  //     hideInMenu: true,
  //   },
  // },
  // {
  //   path: "/:pathMatch(.*)",
  //   redirect: "/404",
  // },
];
