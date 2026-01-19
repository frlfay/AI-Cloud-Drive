// @ts-ignore
/* eslint-disable */
import request from "@/utils/request";
/** 访问分享链接 GET /v1/share/${param0} */
export async function visitShare(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.visitShareParams,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>(`/share/v1/visit`, {
    method: "GET",
    params: { shareId: params.shareId },
    ...(options || {}),
  });
}

/** 取消分享链接 POST /share/v1/cancel */
export async function cancel(
  body: API.CancelShareRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/share/v1/cancel", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 校验分享码 POST /share/v1/check_share_code */
export async function checkShareCode(
  body: API.CheckShareRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/share/v1/check_share_code", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 创建分享链接 POST /share/v1/create */
export async function createShare(
  body: API.CreateShareRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/share/v1/create", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 获取分享详情 GET /v1/share/detail */
export async function getShareDetail(options?: { [key: string]: any }) {
  return request<API.JsonData>("/share/v1/detail", {
    method: "GET",
    ...(options || {}),
  });
}

/** 获取分享详情(需要校验) GET /v1/share/detail-with-code */
export async function getShareDetailWithCode(options?: { [key: string]: any }) {
  return request<API.JsonData>("/v1/share/detail-with-code", {
    method: "GET",
    ...(options || {}),
  });
}

/** 获取分享列表 GET /share/v1/list */
export async function getShareUrl(options?: { [key: string]: any }) {
  return request<API.JsonData>("/share/v1/list", {
    method: "GET",
    ...(options || {}),
  });
}

/** 文件转存 POST /v1/share/save */
export async function saveFiles(
  body: API.SaveShareRequest,
  options?: { [key: string]: any }
) {
  console.log("参数", body);
  return request<API.JsonData>("/share/v1/transfer", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 文件转存（需要校验） POST /v1/share/save-with-code */
export async function saveFilesWithCode(
  body: API.SaveShareRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/v1/share/save-with-code", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 分享文件列表查询 POST /v1/share/share-list */
export async function getShareFileList(
  body: API.ShareFileQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/share/v1/list_share_file", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 分享文件列表查询(需要校验) POST /v1/share/share-list-with-code */
export async function getShareFileListWithCode(
  body: API.ShareFileQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/v1/share/share-list-with-code", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
