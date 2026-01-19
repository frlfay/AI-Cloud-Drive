package net.xdclass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.xdclass.controller.req.*;
import net.xdclass.dto.AccountFileDTO;
import net.xdclass.dto.FileChunkDTO;
import net.xdclass.dto.FolderTreeNodeDTO;
import net.xdclass.interceptor.LoginInterceptor;
import net.xdclass.service.AccountFileService;
import net.xdclass.service.FileChunkService;
import net.xdclass.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
@RestController
@RequestMapping("/api/file/v1")
@Tag(name = "文件管理接口", description = "提供文件和文件夹的管理功能")
public class AccountFileController {

    @Autowired
    private AccountFileService accountFileService;

    @Autowired
    private FileChunkService fileChunkService;

    /**
     * 查询文件列表接口
     */
    @GetMapping("list")
    @Operation(summary = "查询文件列表", description = "根据父文件夹ID查询文件列表")
    public JsonData list(
            @Parameter(description = "父文件夹ID", required = true, example = "1") @RequestParam(value = "parent_id") Long parentId) {
        Long accountId = LoginInterceptor.threadLocal.get().getId();
        List<AccountFileDTO> list = accountFileService.listFile(accountId, parentId);
        return JsonData.buildSuccess(list);
    }

    /**
     * 创建文件夹
     */
    @PostMapping("create_folder")
    @Operation(summary = "创建文件夹", description = "根据请求参数创建文件夹")
    public JsonData createFolder(
            @Parameter(description = "文件夹创建请求对象", required = true) @RequestBody FolderCreateReq req) {
        Long accountId = LoginInterceptor.threadLocal.get().getId();
        req.setAccountId(accountId);
        accountFileService.createFolder(req);
        return JsonData.buildSuccess();
    }

    /**
     * 文件重命名
     */
    @PostMapping("rename_file")
    @Operation(summary = "文件重命名", description = "根据请求参数重命名文件")
    public JsonData renameFile(
            @Parameter(description = "文件重命名请求对象", required = true) @RequestBody FileUpdateReq req) {
        Long accountId = LoginInterceptor.threadLocal.get().getId();
        req.setAccountId(accountId);
        accountFileService.renameFile(req);
        return JsonData.buildSuccess();
    }

    /**
     * 文件树接口
     */
    @GetMapping("/folder/tree")
    @Operation(summary = "获取文件树", description = "获取文件和文件夹的树形结构")
    public JsonData folderTree() {
        Long accountId = LoginInterceptor.threadLocal.get().getId();
        List<FolderTreeNodeDTO> list = accountFileService.folderTree(accountId);
        return JsonData.buildSuccess(list);
    }


    /**
     * 普通小文件上传接口
     */
    @PostMapping("upload")
    @Operation(summary = "普通小文件上传", description = "普通小文件上传")
    public JsonData upload(FileUploadReq req) {
        req.setAccountId(LoginInterceptor.threadLocal.get().getId());
        accountFileService.fileUpload(req);
        return JsonData.buildSuccess();
    }


    /**
     * 文件批量移动
     */
    @PostMapping("move_batch")
    @Operation(summary = "文件批量移动", description = "文件批量移动")
    public JsonData moveBatch(
            @Parameter(description = "文件批量移动请求对象", required = true) @RequestBody FileBatchReq req) {
        req.setAccountId(LoginInterceptor.threadLocal.get().getId());
        accountFileService.moveBatch(req);
        return JsonData.buildSuccess();
    }


    /**
     * 文件批量删除
     */
    @PostMapping("del_batch")
    public JsonData delBatch(@RequestBody FileDelReq req) {
        req.setAccountId(LoginInterceptor.threadLocal.get().getId());

        accountFileService.delBatch(req);

        return JsonData.buildSuccess();
    }


    /**
     * 文件复制接口
     */
    @PostMapping("copy_batch")
    @Operation(summary = "文件批量复制", description = "文件批量复制")
    public JsonData copyBatch(
            @Parameter(description = "文件批量复制请求对象", required = true) @RequestBody FileBatchReq req) {
        req.setAccountId(LoginInterceptor.threadLocal.get().getId());
        accountFileService.copyBatch(req);
        return JsonData.buildSuccess();
    }

    /**
     * 文件秒传接口, true就是文件秒传成功，false失败，需要重新调用上传接口
     */
    @PostMapping("second_upload")
    public JsonData secondUpload(@RequestBody FileSecondUploadReq req) {
        req.setAccountId(LoginInterceptor.threadLocal.get().getId());
        Boolean flag = accountFileService.secondUpload(req);
        return JsonData.buildSuccess(flag);
    }


    /**
     * 1-创建分片上传任务
     */
    @PostMapping("init_file_chunk_task")
    public JsonData initFileChunkTask(@RequestBody FileChunkInitTaskReq req) {
        req.setAccountId(LoginInterceptor.threadLocal.get().getId());
        FileChunkDTO fileChunkDTO = fileChunkService.initFileChunkTask(req);
        return JsonData.buildSuccess(fileChunkDTO);
    }

    /**
     * 2-获取分片上传地址，返回minio临时签名地址
     */
    @GetMapping("/get_file_chunk_upload_url/{identifier}/{partNumber}")
    public JsonData getFileChunkUploadUrl(@PathVariable("identifier") String identifier
            ,@PathVariable("partNumber") int partNumber){

        Long accountId = LoginInterceptor.threadLocal.get().getId();
        String url = fileChunkService.genPreSignUploadUrl(accountId, identifier, partNumber);
        return JsonData.buildSuccess(url);
    }

    /**
     * 3-合并分片
     */
    @PostMapping("merge_file_chunk")
    public JsonData mergeFileChunk(@RequestBody FileChunkMergeReq req) {
        req.setAccountId(LoginInterceptor.threadLocal.get().getId());
        fileChunkService.mergeFileChunk(req);
        return JsonData.buildSuccess();
    }


    /**
     * 查询分片上传进度
     */
    @GetMapping("/chunk_upload_progress/{identifier}")
    public JsonData getUploadProgress(@PathVariable("identifier") String identifier) {
        Long accountId = LoginInterceptor.threadLocal.get().getId();
        FileChunkDTO fileChunkDTO = fileChunkService.listFileChunk(accountId, identifier);
        return JsonData.buildSuccess(fileChunkDTO);
    }


}
