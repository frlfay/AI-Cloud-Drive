declare namespace API {
  type BatchFilesRequest = {
    /** 文件id列表 */
    fileIds?: string[];
    /** 文件路径 */
    targetParentId?: string;
    /** 新路径 */
    accountId?: number;
  };

  type CancelShareRequest = {
    /** 分享ID */
    shareIds?: string[];
    accountId?: number;
  };

  type CheckShareRequest = {
    /** 分享ID */
    shareId: string; // 改为 string 类型
    /** 分享码 */
    shareCode?: string;
  };


  type UploadFile = {
    filename?: string;
    identifier?: string;
    accountId?: number;
    parentId?: string;
    fileSize?: number;
    file?: File;
  }
  type ChunkInitTaskRequest = {
    /** 文件唯一标识 */
    identifier: string;
    /** 文件大小 */
    totalSize: number;
    /** 分片大小 */
    chunkSize: number;
    /** 文件名称 */
    filename: string;
    accountId: number;
  };

  type CreateFileRequest = {
    filePath?: string;
    fileName?: string;
    /** 文件/文件夹名称 */
    folderName?: string;
    /** 文件路径 */
    parentId?: string;
    accountId?: number;
  };

  type CreateShareRequest = {
    /** 分享的名称 */
    shareName?: string;
    /** 分享的类型 */
    shareType?: string;
    /** 分享的日期类型(0 永久, 1 7天, 2 30天 */
    shareDayType?: number;
    /** 分享的文件ID集合，多个使用公用的分割符去拼接 */
    fileIds?: string[];
    accountId?: number;
    /** 分享的路径 */
    host?:string;
  };

  type downloadParams = {
    fileId: string;
  };

  type FileMergeRequest = {
    parentId?: number;
    identifier?: string;
    accountId?: number;
  };

  type FileQueryRequest = {
    /** 当前页号 */
    current?: number;
    /** 页面大小 */
    pageSize?: number;
    /** 排序字段 */
    sortField?: string;
    /** 排序顺序（默认升序） */
    sortOrder?: string;
    userId?: number;
    /** 文件路径 */
    filePath?: string;
    /** 文件名称 */
    fileName?: string;
    /** 文件类型 */
    fileType?: number;
    /** 更新时间 */
    updateTime?: string;
    /** 创建时间 */
    createTime?: string;
    /** 状态 0不是文件夹，1是文件夹 */
    isDirectory?: number;
  };

  type FileUpdateRequest = {
    /** 文件id */
    fileId?: string;
    /** 文件名称 */
    newFilename?: string;
    accountId?: number;
    /** 文件路径/要移动路径 */
    filePath?: string;
    /** 状态 0不是文件夹，1是文件夹 */
    isDirectory?: number;
  };

  type JsonData = {
    code?: number;
    data?: string | any;
    msg?: string;
    success?: boolean;
  };


  type preSignUploadUrlParams = {
    identifier: string;
    partNumber: number;
  };

  type previewParams = {
    fileId: string;
  };

  type SaveShareRequest = {
    /** 分享id */
    shareId?: string;
    /** 分享文件id */
    fileIds?: string[];
    /** 保存路径 */
    accountId?: number;
    /** 当前路径 */
   parentId?: string;
  };

  type ShareFileQueryRequest = {
    parentId?: string;
    /** 当前页号 */
    current?: number;
    /** 页面大小 */
    pageSize?: number;
    /** 排序字段 */
    sortField?: string;
    /** 排序顺序（默认升序） */
    sortOrder?: string;
    /** 文件路径 */
    filePath?: string;
    /** 分享id */
    shareId?: string; // 改为 string 类型
    userId?: number;
    /** 文件名称 */
    fileName?: string;
    /** 文件类型 */
    fileType?: number;
    /** 更新时间 */
    updateTime?: string;
    /** 创建时间 */
    createTime?: string;
    /** 状态 0不是文件夹，1是文件夹 */
    isDirectory?: number;
  };
type downloadUrlParam = {
  fileIds?: string[];
    accountId?: number;
  };
  type downloadUrlEntity = {
    downloadUrl?: string;
    fileName?: string;
  };
  type SecUploadRequest = {
    /** 文件唯一标识 */
    parentId?: string;
    /** 文件名称 */
    filename?: string;
    /** 文件路径 */
    accountId?: string;
    /** 文件大小 */
    identifier?: string;
  };

  type taskInfoParams = {
    identifier: string;
  };

  type updateUserInfoParams = {
    /** 文件 */
    file?: string;
  };

  type FileQuerRequest = {
    /** 文件 */
    parent_id?: number;
  };

  type uploadAvatarParams = FormData;

  type UserLoginRequest = {
    /** 用户名 */
    phone: string;
    /** 用户密码 */
    password: string;
  };

  type UserQueryRequest = {
    /** 当前页号 */
    current?: number;
    /** 页面大小 */
    pageSize?: number;
    /** 排序字段 */
    sortField?: string;
    /** 排序顺序（默认升序） */
    sortOrder?: string;
    userId?: number;
    username?: string;
    role?: string;
    createTime?: string;
    updateTime?: string;
  };

  type UserRegisterRequest = {
    /** 用户名 */
    username?: string;
    /** 用户密码 */
    phone?: string;
    /** 确认密码 */
    password?: string;
    /*  用户头像 */
    avatarUrl?: string;
  };

  type UserUpdateMyRequest = {
    userId?: number;
    username?: string;
    avatarUrl?: string;
    // email?: string;
  };

  type UserUpdateRequest = {
    userId?: number;
    username?: string;
    avatarUrl?: string;
    email?: string;
    capacity?: number;
    role?: number;
  };

  type visitShareParams = {
    shareId: string; // 改为 string 类型
  };

  // ==========用户相关==========

  type UserDTO = {
    /** 用户ID */
    id?: number;
    /* 根文件ID  */
    rootFileId?: string;
    /* 根文件夹名 */
    rootFileName?: string;
    /** 用户名 */
    username?: string;

    /** 用户头像URL */
    avatarUrl?: string;

    /** 用户邮箱 */
    email?: string;

    /** 用户容量大小 */
    capacity?: number;

    /** 用户存储信息 */
    storageDTO: sDTO;

    /** 用户角色 */
    role?: number;

    /** 创建时间 */
    createTime?: string;

    /** 更新时间 */
    updateTime?: string;
  };

  type sDTO = {
    accountId?: string; 
    id? : string;
    totalSize: string;
    usedSize : string;
  }
  // ==========文件相关==========

  type FileDTO = {
    isDir?: number;
    id?: string;
    accountId?: number;
    parentId?: string;
    /** 文件id */
    fileId?: string;

    /** 文件名称 */
    fileName?: string;

    /** 文件类型 */
    fileType?: string;

    /**文件后缀 */
    fileSuffix?: string;

    /** 文件大小 */
    fileSize?: number;

    /** 文件路径 */
    filePath?: string;

    /** 文件url */
    fileUrl?: string;

    /** 唯一标识 */
    fileIdentifier?: string;

    /** 存储桶名 */
    fileBucketName?: string;

    /** 文件在 minio 中的 objectKey */
    objectKey?: string;

    /** 文件预览的响应头 Content-Type 的值 */
    filePreviewContentType?: string;

    /** 更新时间 */
    updateTime?: string;
    gmtModified?: string;

    /** 状态 0不是文件夹，1是文件夹 */
    isDirectory?: number;
  };

  // ==========分片关==========

  type FileChunkInfoDTO = {
    /** 是否完成上传（是否已经合并分片） */
    finished?: boolean;

    /** 文件地址 */
    path?: string;

    /** 上传记录 */
    taskRecord?: FileChunkRecordDTO;
  };

  type FileChunkRecordDTO = FileChunkDO & {
    /** 已上传完的分片 */
    exitPartList?: PartSummary[];
  };

  type FileChunkDO = {
    /** 分片ID */
    id?: number;

    /** 分片上传的uploadId */
    uploadId?: string;

    /** 文件唯一标识（md5） */
    fileIdentifier?: string;

    /** 文件名 */
    fileName?: string;

    /** 所属桶名 */
    bucketName?: string;

    /** 文件的key */
    objectKey?: string;

    /** 文件大小（byte） */
    totalSize?: number;

    /** 每个分片大小（byte） */
    chunkSize?: number;

    /** 分片数量 */
    chunkNum?: number;

    /** 用户ID */
    userId?: number;
  };

  type PartSummary = {
    /** 分片编号 */
    partNumber?: number;

    /** 最后修改时间 */
    lastModified?: string;

    /** ETag */
    eTag?: string;

    /** 分片大小 */
    size?: number;
  };

  type TreeNodeDTO = {
    /** 节点id */
    id: string;

    /** 节点名 */
    label?: string;

    /** 深度 */
    depth?: number;

    /** 是否被关闭 */
    state?: string;

    /** 属性集合 */
    attributes?: Record<string, string>;

    /** 子节点列表 */
    children?: TreeNodeDTO[];
  };

  // ==========存储相关==========

  type StorageDTO = {
    /** 存储ID */
    storageId?: number;

    /** 所属用户ID */
    userId?: number;

    /** 占用存储大小 */
    storageSize?: number;

    /** 总存储大小 */
    totalStorageSize?: number;

    /** 操作权限 */
    permission?: number;

    /** 创建时间 */
    createTime?: string;

    /** 更新时间 */
    updateTime?: string;
  };

  // ==========分享相关==========
  type ShareDTO = {
    /** 分享ID */
    shareId?: string;

    id?: string;
    /** 分享名称 */
    shareName?: string;

    /** 分享链接URL */
    shareUrl?: string;

    /** 分享码 */
    shareCode?: string;

    /** 分享状态 */
    shareStatus?: number;

    /** 分享的类型 */
    shareType?: string;

    /** 分享的过期类型 */
    shareDayType?: number;

    /** 分享的过期时间 */
    shareEndTime?: string;

    /** 分享的创建时间 */
    createTime?: string;
  };

  type ShareDetailDTO = {
    /** 分享ID */
    shareId?: string;

    id: string;

    /** 分享名称 */
    shareName?: string;

    /** 分享链接URL */
    shareUrl?: string;

    /** 分享码 */
    shareCode?: string;

    /** 分享状态*/
    shareStatus?: number;

    /** 分享的类型 */
    shareType?: string;

    /** 分享的过期类型 */
    shareDayType?: number;

    /** 分享的截止时间 */
    shareEndTime?: string;

    /** 分享的创建时间 */
    createTime?: string;

    /** 分享的文件列表 */
    fileDTOList?: FileDTO[];

    /** 分享者的信息 */
    shareAccountDTO?: shareUserDTO;
  };
  type shareUserDTO = {
    /** 分享ID */
    id?: string;

    /** 分享名称 */
    username?: string;

    avatarUrl?: string;

  }
    /** 分享的类型 */
  type ShareSimpleDTO = {
    /** 分享ID */
    shareId?: string;

    /** 分享名称 */
    shareName?: string;

    /** 分享链接URL */
    shareUrl?: string;

    /** 分享状态 */
    shareStatus?: number;

    /** 分享的类型 */
    shareType?: string;

    /** 分享的过期类型 */
    shareDayType?: number;

    /** 分享的过期时间 */
    shareEndTime?: string;

    /** 分享的创建时间 */
    createTime?: string;
  };

  type ShareUrlDTO = {
    /** 分享链接ID */
    shareId?: string;

    /** 分享链接名称 */
    shareName?: string;

    /** 分享链接 */
    shareUrl?: string;

    /** 分享码 */
    shareCode?: string;

    /** 分享状态 */
    shareStatus?: number;
  };
}
