// @ts-ignore
/* eslint-disable */
import request from "@/utils/request";
import { useFileStores } from "@/store/filePath";
import qs from 'qs';
/** 分页查询文件列表 POST /v1/file/list */

export async function listFiles(
  params: {
    parent_id: any
  },
  options?: { [key: string]: any }
) {
  console.log("parent_id:", params.parent_id);
  useFileStores().addFilePath(params.parent_id.toString())
  localStorage.setItem("parent_id", params.parent_id.toString())
  return request<API.JsonData>("/file/v1/list", {
    method: "GET",
    params: {
      parent_id: params.parent_id,
      // parentId: params.parentId,
    },
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    ...(options || {}),
  });
}


/**根据条件查询文件列表 */
export async function searchFilesByName(
  params: {
    search: any
  },
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/file/v1/search", {
    method: "GET",
    params: {
      search: params.search,
      // parentId: params.parentId,
    },
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    ...(options || {}),
  });
}


/** 获取文件树 GET /v1/file/get-file-tree */
export async function getFileTree(options?: { [key: string]: any }) {
  return request<API.JsonData>("/file/v1/folder/tree", {
    method: "GET",
    ...(options || {}),
  });
}

/** 创建文件夹 POST /file/v1/create_folder */
export async function createFolder(
  body: API.CreateFileRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/file/v1/create_folder", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}


/** 1.获取分片上传进度 GET /file/v1/chunk_upload_progress/{identifier} */
export async function taskInfo(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.taskInfoParams,
  options?: { [key: string]: any }
) {
  const { identifier: param0, ...queryParams } = params;
  return request<API.JsonData>(`/file/v1/chunk_upload_progress/${param0}`, {
    method: "GET",
    params: { ...queryParams },
    ...(options || {}),
  });
}


/** 2.创建分片上传任务 POST /v1/file */
export async function initTask(
  body: API.ChunkInitTaskRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/file/v1/init_file_chunk_task", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 3.获取分片上传地址 GET /file/v1/get_file_chunk_upload_url/{identifier}/{partNumber} */
export async function preSignUploadUrl(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.preSignUploadUrlParams,
  options?: { [key: string]: any }
) {
  const { identifier: param0, partNumber: param1, ...queryParams } = params;
  return request<API.JsonData>(`/file/v1/get_file_chunk_upload_url/${param0}/${param1}`, {
    method: "GET",
    params: { ...queryParams },
    ...(options || {}),
  });
}


/** 4.合并分片 POST /file/v1/merge_file_chunk */
export async function merge(
  body: API.FileMergeRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/file/v1/merge_file_chunk", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 批量删除文件 POST /file/v1/del_batch */
export async function delFiles(
  body: API.BatchFilesRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/file/v1/del_batch", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 批量移动文件 POST /file/v1/move_batch */
export async function moveFiles(
  body: API.BatchFilesRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/file/v1/move_batch", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 批量复制文件 POST /file/v1/copy_batch */
export async function copyFiles(
  body: API.BatchFilesRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/file/v1/copy_batch", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 文件重命名 POST /file/v1/rename_file */
export async function renameFile(
  body: API.FileUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/file/v1/rename_file", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 获得文件下载地址 GET /v1/file/getDownloadUrl */
export async function downloadUrlParam(
  body: API.BatchFilesRequest,
  options?: { [key: string]: any }
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  
) {
  return request<API.JsonData>("/file/v1/batch_download_url", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 文件预览 GET /v1/file/preview */
export async function preview(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.previewParams,
  options?: { [key: string]: any }
) {
  return request<any>("/v1/file/preview", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 文件秒传 POST /file/v1/second_upload */
export async function secUpload(
  body: API.SecUploadRequest,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/file/v1/second_upload", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
// 单文件上传 /file/v1/upload
export async function uploadFiles(
  data: API.UploadFile,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/file/v1/upload", {
    method: "POST",
    data: data,
    headers: {
      "Content-Type": "multipart/form-data",
    },
    ...(options || {}),
  });
}


// 单文件下载 /file/v1/download
export async function downloadFiles(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.downloadUrlParam,
  options?: { [key: string]: any }
) {
  return request<API.JsonData>("/file/v1/download", {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    params: {
      ...params,
    },
    paramsSerializer: (params) => qs.stringify(params, { indices: false }),
    responseType: "blob",
    timeout: 600000,
    ...(options || {}),
  });
}

