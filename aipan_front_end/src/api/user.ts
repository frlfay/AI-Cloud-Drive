// @ts-ignore
/* eslint-disable */
import request from "@/utils/request";

/** 获取当前用户信息 GET /account/v1/detail */
export async function currentUser(options?: { [key: string]: any }) {
  return request<API.JsonData>("/account/v1/detail", {
    method: "GET",
    ...(options || {}),
  });
}



// /** 获取用户存储容量 GET /v1/user/getStorage */
// export async function getStorage(options?: { [key: string]: any }) {
//   return request<API.JsonData>("/v1/user/getStorage", {
//     method: "GET",
//     ...(options || {}),
//   });
// }

/** 分页获取用户列表（管理员） POST /v1/user/list */
export async function listUsersByPage(
  body: API.UserQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/v1/user/list", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 用户登录 POST /v1/user/login */
export async function userLogin(
  body: API.UserLoginRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/account/v1/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}


/** 用户注册 POST /v1/user/register */
export async function userRegister(
  body: API.UserRegisterRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/account/v1/register", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 更新用户信息 (管理员) POST /v1/user/update */
export async function updateUser(
  body: API.UserUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/v1/user/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 更新用户信息 POST /v1/user/update/my */
export async function updateUserInfo(
  params: API.updateUserInfoParams,
  body: API.UserUpdateMyRequest,
  options?: { [key: string]: any }
) {
  console.log("params:", params);
  console.log("body:", body);
  return request<API.JsonData>("/account/v1/update/my", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    params: {
      ...params,
    },
    data: body,
    ...(options || {}),
  });
}

/** 上传用户头像 POST /v1/user/uploadAvatar */
export async function uploadAvatar(
  formData: FormData,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/account/v1/upload_avatar", {
    method: "POST",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
    ...(options || {}),
  });
}
