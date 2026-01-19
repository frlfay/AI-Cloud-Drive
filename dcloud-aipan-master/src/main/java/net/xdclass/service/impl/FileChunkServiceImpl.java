package net.xdclass.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.component.StoreEngine;
import net.xdclass.config.MinioConfig;
import net.xdclass.controller.req.FileChunkInitTaskReq;
import net.xdclass.controller.req.FileChunkMergeReq;
import net.xdclass.controller.req.FileUploadReq;
import net.xdclass.dto.FileChunkDTO;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.exception.BizException;
import net.xdclass.mapper.FileChunkMapper;
import net.xdclass.mapper.StorageMapper;
import net.xdclass.model.FileChunkDO;
import net.xdclass.model.StorageDO;
import net.xdclass.service.AccountFileService;
import net.xdclass.service.FileChunkService;
import net.xdclass.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
@Service
@Slf4j
public class FileChunkServiceImpl implements FileChunkService {

    @Autowired
    private StorageMapper storageMapper;


    @Autowired
    private StoreEngine fileStoreEngine;

    @Autowired
    private FileChunkMapper fileChunkMapper;


    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private AccountFileService accountFileService;


    /**
     * * 检查存储空间是否够( 合并文件的时候进行校验更新存储空间)
     * * 根据文件名推断内容类型
     * * 初始化分片上传,获取上传ID
     * * 创建上传任务实体并设置相关属性
     * * 将任务插入数据库，构建并返回任务信息DTO
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileChunkDTO initFileChunkTask(FileChunkInitTaskReq req) {

        //检查存储空间是否够( 合并文件的时候进行校验更新存储空间)
        StorageDO storageDO = storageMapper.selectOne(new QueryWrapper<>(new StorageDO())
                .eq("account_id", req.getAccountId()));
        if(storageDO.getUsedSize() + req.getTotalSize() > storageDO.getTotalSize()){
            throw new BizException(BizCodeEnum.FILE_STORAGE_NOT_ENOUGH);
        }

        String objectKey = CommonUtil.getFilePath(req.getFilename());
        //获取文件类型
        String contentType = MediaTypeFactory.getMediaType(objectKey).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
        //配置下元数据
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);

        //初始化分片上传，获取ID
        InitiateMultipartUploadResult uploadResult = fileStoreEngine.initMultipartUploadTask(minioConfig.getBucketName(), objectKey, metadata);
        String uploadId = uploadResult.getUploadId();

        int chunkNum = (int)Math.ceil(req.getTotalSize() * 1.0 / req.getChunkSize());
        FileChunkDO task = new FileChunkDO();
        task.setBucketName(minioConfig.getBucketName())
                .setChunkNum(chunkNum)
                .setChunkSize(req.getChunkSize())
                .setTotalSize(req.getTotalSize())
                .setFileName(req.getFilename())
                .setIdentifier(req.getIdentifier())
                .setObjectKey(objectKey)
                .setUploadId(uploadId)
                .setAccountId(req.getAccountId());

        //保存到数据库
        fileChunkMapper.insert(task);


        return new FileChunkDTO(task).setFinished(false).setExitPartList(new ArrayList<>());
    }

    @Override
    public String genPreSignUploadUrl(Long accountId, String identifier, int partNumber) {

        FileChunkDO task = fileChunkMapper.selectOne(new QueryWrapper<FileChunkDO>()
                .eq("account_id", accountId)
                .eq("identifier", identifier));

        if(task == null){
            throw new BizException(BizCodeEnum.FILE_CHUNK_TASK_NOT_EXISTS);
        }

        //配置预签名过期时间
        DateTime expireTime = DateUtil.offsetMillisecond(new Date(), minioConfig.getPreSignUrlExpireTime().intValue());

        //生成签名URL
        Map<String,Object> params = new HashMap<>();
        params.put("partNumber",partNumber);
        params.put("uploadId",task.getUploadId());
        URL preSignedUrl = fileStoreEngine
                .genePreSignedUrl(task.getBucketName(), task.getObjectKey(), HttpMethod.PUT, expireTime, params);

        log.info("preSignedUrl:{}",preSignedUrl);

        return preSignedUrl.toString();
    }

    /**
     * *  获取任务和分片列表，检查是否足够合并
     * *  检查存储空间和更新
     * *  合并分片
     * *  判断合并分片是否成功
     * *  存储文件和关联信息到数据库
     * *  根据唯一标识符删除相关分片信息
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mergeFileChunk(FileChunkMergeReq req) {

        //获取任务和分片列表，检查是否足够合并
        FileChunkDO task = fileChunkMapper.selectOne(new QueryWrapper<FileChunkDO>()
                .eq("account_id", req.getAccountId())
                .eq("identifier", req.getIdentifier()));
        if(task == null){
            throw new BizException(BizCodeEnum.FILE_CHUNK_TASK_NOT_EXISTS);
        }

        PartListing partListing = fileStoreEngine.listMultipart(task.getBucketName(), task.getObjectKey(), task.getUploadId());
        List<PartSummary> parts = partListing.getParts();
        if(parts.size() != task.getChunkNum()){
            //上传的分片数量和记录中不对应，合并失败
            throw new BizException(BizCodeEnum.FILE_CHUNK_NOT_ENOUGH);
        }

        //检查更新存储空间
        StorageDO storageDO = storageMapper.selectOne(new QueryWrapper<>(new StorageDO())
                .eq("account_id", req.getAccountId()));
        long realFileTotalSize = parts.stream().map(PartSummary::getSize).mapToLong(Long::valueOf).sum();
        if(storageDO.getUsedSize() + realFileTotalSize > storageDO.getTotalSize()){
            throw new BizException(BizCodeEnum.FILE_STORAGE_NOT_ENOUGH);
        }
        storageDO.setUsedSize(storageDO.getUsedSize() +realFileTotalSize);
        storageMapper.updateById(storageDO);

        //2-合并文件
        CompleteMultipartUploadResult result = fileStoreEngine.mergeChunks(task.getBucketName(),
                task.getObjectKey(), task.getUploadId(),
                parts.stream().map(partSummary ->
                                new PartETag(partSummary.getPartNumber(), partSummary.getETag()))
                        .collect(Collectors.toList()));

        //【判断是否合并成功
        if(result.getETag()!=null){
            FileUploadReq fileUploadReq = new FileUploadReq();
            fileUploadReq.setAccountId(req.getAccountId())
                    .setFilename(task.getFileName())
                    .setIdentifier(task.getIdentifier())
                    .setParentId(req.getParentId())
                    .setFileSize(realFileTotalSize)
                    .setFile(null);

            //存储文件和关联信息到数据库
            accountFileService.saveFileAndAccountFile(fileUploadReq,task.getObjectKey());

            //删除相关任务记录
            fileChunkMapper.deleteById(task.getId());

            log.info("合并成功");
        }
    }

    @Override
    public FileChunkDTO listFileChunk(Long accountId, String identifier) {
        //查询任务是否存在
        FileChunkDO task = fileChunkMapper.selectOne(new QueryWrapper<FileChunkDO>()
                .eq("account_id", accountId)
                .eq("identifier", identifier));
        if(task == null){
            throw new BizException(BizCodeEnum.FILE_CHUNK_TASK_NOT_EXISTS);
        }

        FileChunkDTO result = new FileChunkDTO(task);

        //判断文件服务器那边是否存在
        boolean objectExist = fileStoreEngine.doesObjectExist(task.getBucketName(), task.getObjectKey());
        if(!objectExist){
            //不存在，就是未上传完成，返回已经上传的分片概述
            PartListing partListing = fileStoreEngine.listMultipart(task.getBucketName(), task.getObjectKey(), task.getUploadId());

            if(partListing.getParts().size() == task.getChunkNum()){
                //已经上传完成，可以合并
                result.setFinished(true).setExitPartList(partListing.getParts());
            }else {
                //  未上传完成，还不能合并
                result.setFinished(false).setExitPartList(partListing.getParts());
            }
        }
        return result;
    }
}