package net.xdclass.service;

import net.xdclass.controller.req.FileChunkInitTaskReq;
import net.xdclass.controller.req.FileChunkMergeReq;
import net.xdclass.dto.FileChunkDTO;

public interface FileChunkService {
    /**
     * 初始化分片上传
     * @param req
     * @return
     */
    FileChunkDTO initFileChunkTask(FileChunkInitTaskReq req);


    /**
     * 获取临时文件上传地址
     * @param accountId
     * @param identifier
     * @param partNumber
     * @return
     */
    String genPreSignUploadUrl(Long accountId, String identifier, int partNumber);

    /**
     * 合并分片
     * @param req
     */
    void mergeFileChunk(FileChunkMergeReq req);

    /**
     * 查询分片上传进度
     * @param accountId
     * @param identifier
     * @return
     */
    FileChunkDTO listFileChunk(Long accountId, String identifier);
}
