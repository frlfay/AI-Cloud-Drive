// @ts-ignore
/* eslint-disable */
import request from "@/utils/request";

/** 批量彻底删除 POST /v1/recycle/delete/batch */
export async function batchDelete(
  body: API.BatchFilesRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/recycle/v1/delete", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 文件查询 GET /v1/recycle/list */
export async function listRecycleFiles(options?: { [key: string]: any }) {
  return request<API.JsonData>("/recycle/v1/list", {
    method: "GET",
    ...(options || {}),
  });
}

/** 批量文件还原 POST /v1/recycle/restore/batch */
export async function batchRestore(
  body: API.BatchFilesRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/recycle/v1/restore", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
