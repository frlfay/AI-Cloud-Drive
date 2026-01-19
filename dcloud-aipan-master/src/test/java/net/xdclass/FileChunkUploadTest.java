package net.xdclass;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.controller.req.FileChunkInitTaskReq;
import net.xdclass.controller.req.FileChunkMergeReq;
import net.xdclass.dto.FileChunkDTO;
import net.xdclass.service.FileChunkService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
@SpringBootTest
@Slf4j
public class FileChunkUploadTest {

    @Autowired
    private FileChunkService fileChunkService;

    private Long accountId = 1877928107881652225L;

    private String identifier = "dddfdsfadsfadsfasddd";

    /**
     * 存储分片后端文件路径
     */
    private final List<String> chunkFilePaths = new ArrayList<>();

    /**
     * 存储分片上传的临时签名地址
     */
    private final List<String> chunkUploadUrls = new ArrayList<>();

    /**
     * 上传ID
     */
    private String uploadId;

    /**
     * 分片大小 5MB
     */
    private final long chunkSize = 1024 * 1024 * 5;


    /**
     * 文件分片处理，生成小的chunk文件
     */
    @Test
    public void testCreateFileChunk() {
        //将文件分片并存储
        String filepath = "/Users/xdclass/Desktop/chunk/es_note.pdf";
        File file = new File(filepath);
        long filesize = file.length();

        //计算分片数量
        int chunkNum = (int) Math.ceil(filesize * 1.0 / chunkSize);
        log.info("分片数量:{}",chunkNum);
        try(FileInputStream fis = new FileInputStream(file)){
            byte[] buffer = new byte[(int)chunkSize];
            for(int i = 0; i<chunkNum ; i++){
                String chunkFileName = filepath + ".part"+(i+1);
                try(FileOutputStream fos = new FileOutputStream(chunkFileName)){
                    int bytesRead = fis.read(buffer);
                    fos.write(buffer,0,bytesRead);
                    log.info("创建分片文件:{}，大小{}",chunkFileName,bytesRead);
                    chunkFilePaths.add(chunkFileName);
                    log.info("分片文件存储路径:{}",chunkFilePaths);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //创建分片上传任务
        testInitFileChunkTask();

    }

    /**
     * 1-创建分片上传任务
     */
    private void testInitFileChunkTask() {
        FileChunkInitTaskReq req = new FileChunkInitTaskReq();
        req.setAccountId(accountId).setFilename("es_note.pdf")
                .setIdentifier(identifier)
                .setTotalSize((long)20552959)
                .setChunkSize(chunkSize);
        FileChunkDTO fileChunkDTO = fileChunkService.initFileChunkTask(req);
        log.info("初始化分片上传任务:{}",fileChunkDTO);

        uploadId = fileChunkDTO.getUploadId();

        //获取分片临时上传地址
        testGetFileChunkUploadUrl();
    }

    /**
     * 2-获取分片临时上传地址
     */
    private void testGetFileChunkUploadUrl() {
        for(int i =1; i<=chunkFilePaths.size(); i++){
            String uploadUrl = fileChunkService.genPreSignUploadUrl(accountId, identifier, i);
            log.info("分片上传地址:{}",uploadUrl);
            //存储分片上传地址
            chunkUploadUrls.add(uploadUrl);
        }

        //上传分片文件，模拟前端上传
        uploadChunk();

    }

    /**
     * 模拟前端上传分片文件到minio
     */
    @SneakyThrows
    private void uploadChunk() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        for(int i=0; i<chunkUploadUrls.size(); i++){
            //使用对应put方法直接上传
            String uploadUrl = chunkUploadUrls.get(i);
            HttpPut httpPut = new HttpPut(uploadUrl);
            httpPut.setHeader("Content-Type","application/octet-stream");
            File chunkFile = new File(chunkFilePaths.get(i));
            FileEntity chunkFileEntity = new FileEntity(chunkFile);
            httpPut.setEntity(chunkFileEntity);
            CloseableHttpResponse response = httpClient.execute(httpPut);
            log.info("分片上传状态:{}",response.getStatusLine());
        }

    }

    /**
     * 大文件合并接口测试
     */
    @Test
    public void testMergeFileChunk(){
        FileChunkMergeReq req = new FileChunkMergeReq();
        req.setAccountId(accountId).setIdentifier(identifier).setParentId(1877928108187836417L);
        fileChunkService.mergeFileChunk(req);
    }

    /**
     * 查询文件上传进度测试
     */
    @Test
    public void testChunkUploadProgress(){
        FileChunkDTO fileChunkDTO = fileChunkService.listFileChunk(accountId, identifier);
        log.info("查询文件上传进度:{}",fileChunkDTO);
    }


}