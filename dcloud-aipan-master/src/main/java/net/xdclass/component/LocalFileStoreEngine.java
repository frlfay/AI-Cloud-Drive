package net.xdclass.component;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
//@Component
public class LocalFileStoreEngine implements StoreEngine{
    @Override
    public boolean bucketExists(String bucketName) {
        return false;
    }

    @Override
    public boolean removeBucket(String bucketName) {
        return false;
    }

    @Override
    public void createBucket(String bucketName) {

    }

    @Override
    public List<Bucket> getAllBucket() {
        return List.of();
    }

    @Override
    public List<S3ObjectSummary> listObjects(String bucketName) {
        return List.of();
    }

    @Override
    public boolean doesObjectExist(String bucketName, String objectKey) {
        return false;
    }

    @Override
    public boolean upload(String bucketName, String objectKey, String localFileName) {
        return false;
    }

    @Override
    public boolean upload(String bucketName, String objectKey, MultipartFile file) {
        return false;
    }

    @Override
    public boolean delete(String bucketName, String objectKey) {
        return false;
    }

    @Override
    public String getDownloadUrl(String bucketName, String remoteFileName, long timeout, TimeUnit unit) {
        return "";
    }

    @Override
    public void download2Response(String bucketName, String objectKey, HttpServletResponse response) {

    }

    @Override
    public PartListing listMultipart(String bucketName, String objectKey, String uploadId) {
        return null;
    }

    @Override
    public InitiateMultipartUploadResult initMultipartUploadTask(String bucketName, String objectKey, ObjectMetadata metadata) {
        return null;
    }

    @Override
    public URL genePreSignedUrl(String bucketName, String objectKey, HttpMethod httpMethod, Date expiration, Map<String, Object> params) {
        return null;
    }

    @Override
    public CompleteMultipartUploadResult mergeChunks(String bucketName, String objectKey, String uploadId, List<PartETag> partETags) {
        return null;
    }
}