package net.xdclass.component;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description MinIO 文件存储引擎实现类
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
@Component
@Slf4j
public class MinIOFileStoreEngine implements StoreEngine {

    @Resource
    private AmazonS3Client amazonS3Client;

    // 缓存 bucket 存在性
    private final Map<String, Boolean> bucketCache = new ConcurrentHashMap<>();

    /**
     * 检查 bucket 是否存在
     *
     * @param bucketName bucket 名称
     * @return 如果 bucket 存在返回 true，否则返回 false
     */
    @Override
    public boolean bucketExists(String bucketName) {
        return bucketCache.computeIfAbsent(bucketName, key -> amazonS3Client.doesBucketExistV2(key));
    }

    /**
     * 删除 bucket
     *
     * @param bucketName bucket 名称
     * @return 如果删除成功返回 true，否则返回 false
     */
    @Override
    public boolean removeBucket(String bucketName) {
        if (bucketExists(bucketName)) {
            try {
                amazonS3Client.deleteBucket(bucketName);
                bucketCache.remove(bucketName);
                return true;
            } catch (Exception e) {
                log.error("删除 bucket {} 失败: {}", bucketName, e.getMessage(), e);
            }
        }
        return false;
    }

    /**
     * 创建 bucket
     *
     * @param bucketName bucket 名称
     */
    @Override
    public void createBucket(String bucketName) {
        log.info("创建 bucket: {}", bucketName);
        if (!bucketExists(bucketName)) {
            try {
                amazonS3Client.createBucket(bucketName);
                bucketCache.put(bucketName, true);
            } catch (Exception e) {
                log.error("创建 bucket {} 失败: {}", bucketName, e.getMessage(), e);
            }
        } else {
            log.info("bucket 已存在");
        }
    }

    /**
     * 获取所有 bucket 列表
     *
     * @return bucket 列表
     */
    @Override
    public List<Bucket> getAllBucket() {
        return amazonS3Client.listBuckets();
    }

    /**
     * 列出 bucket 中的所有对象
     *
     * @param bucketName bucket 名称
     * @return 对象列表
     */
    @Override
    public List<S3ObjectSummary> listObjects(String bucketName) {
        if (bucketExists(bucketName)) {
            return amazonS3Client.listObjects(bucketName).getObjectSummaries();
        }
        return List.of();
    }

    /**
     * 检查对象是否存在
     *
     * @param bucketName bucket 名称
     * @param objectKey  对象键
     * @return 如果对象存在返回 true，否则返回 false
     */
    @Override
    public boolean doesObjectExist(String bucketName, String objectKey) {
        if (bucketExists(bucketName)) {
            return amazonS3Client.doesObjectExist(bucketName, objectKey);
        }
        return false;
    }

    /**
     * 上传本地文件到 bucket
     *
     * @param bucketName bucket 名称
     * @param objectKey  对象键
     * @param localFileName 本地文件路径
     * @return 如果上传成功返回 true，否则返回 false
     */
    @Override
    public boolean upload(String bucketName, String objectKey, String localFileName) {
        if (bucketExists(bucketName)) {
            try {
                amazonS3Client.putObject(bucketName, objectKey, new File(localFileName));
                return true;
            } catch (Exception e) {
                log.error("上传文件到 bucket {} 失败: {}", bucketName, e.getMessage(), e);
            }
        }
        return false;
    }

    /**
     * 上传 MultipartFile 到 bucket
     *
     * @param bucketName bucket 名称
     * @param objectKey  对象键
     * @param file       MultipartFile 对象
     * @return 如果上传成功返回 true，否则返回 false
     */
    @Override
    public boolean upload(String bucketName, String objectKey, MultipartFile file) {
        if (bucketExists(bucketName)) {
            try {
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(file.getContentType());
                objectMetadata.setContentLength(file.getSize());
                amazonS3Client.putObject(bucketName, objectKey, file.getInputStream(), objectMetadata);
                return true;
            } catch (Exception e) {
                log.error("上传文件到 bucket {} 失败: {}", bucketName, e.getMessage(), e);
            }
        }
        return false;
    }

    /**
     * 删除 bucket 中的对象
     *
     * @param bucketName bucket 名称
     * @param objectKey  对象键
     * @return 如果删除成功返回 true，否则返回 false
     */
    @Override
    public boolean delete(String bucketName, String objectKey) {
        if (bucketExists(bucketName)) {
            try {
                amazonS3Client.deleteObject(bucketName, objectKey);
                return true;
            } catch (Exception e) {
                log.error("删除 bucket {} 中的对象 {} 失败: {}", bucketName, objectKey, e.getMessage(), e);
            }
        }
        return false;
    }

    /**
     * 获取对象的下载 URL
     *
     * @param bucketName bucket 名称
     * @param objectKey  对象键
     * @param timeout    过期时间
     * @param unit       时间单位
     * @return 下载 URL 字符串
     */
    @Override
    public String getDownloadUrl(String bucketName, String objectKey, long timeout, TimeUnit unit) {
        try {
            Date expiration = new Date(System.currentTimeMillis() + unit.toMillis(timeout));
            return amazonS3Client.generatePresignedUrl(bucketName, objectKey, expiration).toString();
        } catch (Exception e) {
            log.error("生成 bucket {} 中对象 {} 的下载 URL 失败: {}", bucketName, objectKey, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 下载对象到 HttpServletResponse
     *
     * @param bucketName bucket 名称
     * @param objectKey  对象键
     * @param response   HttpServletResponse 对象
     */
    @Override
    public void download2Response(String bucketName, String objectKey, HttpServletResponse response) {
        try (S3Object s3Object = amazonS3Client.getObject(bucketName, objectKey)) {
            response.setHeader("Content-Disposition", "attachment;filename=" + objectKey.substring(objectKey.lastIndexOf("/") + 1));
            response.setContentType("application/force-download");
            response.setCharacterEncoding("UTF-8");
            IOUtils.copy(s3Object.getObjectContent(), response.getOutputStream());
        } catch (IOException e) {
            log.error("下载 bucket {} 中对象 {} 失败: {}", bucketName, objectKey, e.getMessage(), e);
        }
    }


    @Override
    public PartListing listMultipart(String bucketName, String objectKey, String uploadId) {
        try {
            ListPartsRequest request = new ListPartsRequest(bucketName, objectKey, uploadId);
            return amazonS3Client.listParts(request);
        } catch (Exception e) {
            log.error("errorMsg={}", e);
            return null;
        }
    }

    @Override
    public InitiateMultipartUploadResult initMultipartUploadTask(String bucketName, String objectKey, ObjectMetadata metadata) {
        try {
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectKey, metadata);
            return amazonS3Client.initiateMultipartUpload(request);
        } catch (Exception e) {
            log.error("errorMsg={}", e);
            return null;
        }
    }


    @Override
    public URL genePreSignedUrl(String bucketName, String objectKey, HttpMethod httpMethod, Date expiration, Map<String, Object> params) {
        try {
            GeneratePresignedUrlRequest genePreSignedUrlReq =
                    new GeneratePresignedUrlRequest(bucketName, objectKey, httpMethod)
                            .withExpiration(expiration);
            //遍历params作为参数加到genePreSignedUrlReq里面，比如 添加上传ID和分片编号作为请求参数
            //genePreSignedUrlReq.addRequestParameter("uploadId", uploadId);
            //genePreSignedUrlReq.addRequestParameter("partNumber", String.valueOf(i));
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                genePreSignedUrlReq.addRequestParameter(entry.getKey(), String.valueOf(entry.getValue()));
            }
            // 生成并获取预签名URL
            return amazonS3Client.generatePresignedUrl(genePreSignedUrlReq);
        } catch (Exception e) {
            log.error("errorMsg={}", e);
            return null;
        }
    }

    @Override
    public CompleteMultipartUploadResult mergeChunks(String bucketName, String objectKey, String uploadId, List<PartETag> partETags) {
        CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(bucketName, objectKey, uploadId, partETags);
        return amazonS3Client.completeMultipartUpload(request);

    }
}
