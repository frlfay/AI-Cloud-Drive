package net.xdclass.component;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface StoreEngine {

    /*=====================Bucket相关===========================*/

    /**
     * 检查指定的存储桶是否存在于当前的存储系统中
     *
     * @param bucketName 存储桶的名称
     * @return 如果存储桶存在，则返回true；否则返回false
     */
    boolean bucketExists(String bucketName);

    /**
     * 删除指定名称的存储桶
     *
     * @param bucketName 存储桶的名称
     * @return 如果存储桶删除成功，则返回true；否则返回false
     */
    boolean removeBucket(String bucketName);

    /**
     * 创建一个新的存储桶
     *
     * @param bucketName 新存储桶的名称
     */
    void createBucket(String bucketName);

    /**
     * 获取当前存储系统中的所有存储桶列表
     *
     * @return 包含所有存储桶的列表
     */
    List<Bucket> getAllBucket();

    /*===================文件处理相关=============================*/

    /**
     * 列出指定桶中的所有对象
     *
     * @param bucketName 桶名称
     * @return 包含桶中所有对象摘要的列表
     */
    List<S3ObjectSummary> listObjects(String bucketName);


    /**
     * 判断文件是否存在
     */
    boolean doesObjectExist(String bucketName, String objectKey);

    /**
     * 将本地文件上传到指定桶
     *
     * @param bucketName 桶名称
     * @param objectKey 上传后对象的名称
     * @param localFileName 本地文件的路径
     * @return 上传是否成功
     */
    boolean upload(String bucketName, String objectKey, String localFileName);

    /**
     * 将multipart文件上传到指定桶
     *
     * @param bucketName 桶名称
     * @param objectKey 上传后对象的名称
     * @param file 要上传的multipart文件
     * @return 上传是否成功
     */
    boolean upload(String bucketName, String objectKey, MultipartFile file);

    /**
     * 从指定桶中删除对象
     *
     * @param bucketName 桶名称
     * @param objectKey 要删除的对象的名称
     * @return 删除是否成功
     */
    boolean delete(String bucketName, String objectKey);

    /*===================下载相关=============================*/
    /**
     * 获取指定对象的下载URL
     *
     * @param bucketName 桶名称
     * @param remoteFileName 对象的名称
     * @param timeout URL的有效时长
     * @param unit URL有效时长的时间单位
     * @return 对象的下载URL
     */
    String getDownloadUrl(String bucketName, String remoteFileName, long timeout, TimeUnit unit);

    /**
     * 将指定对象下载到HTTP响应中
     *
     * @param bucketName 桶名称
     * @param objectKey 对象的名称
     * @param response HTTP响应对象，用于输出下载的对象
     */
    void download2Response(String bucketName, String objectKey, HttpServletResponse response);


    //==================大文件分片上传相关接口=========================

    /**
     * 查询分片数据
     * @param bucketName 存储桶名称
     * @param objectKey 对象名称
     * @param uploadId 分片上传ID
     * @return 分片列表对象
     */
    PartListing listMultipart(String bucketName, String objectKey, String uploadId);

    /**
     * 1-初始化分片上传任务,获取uploadId,如果初始化时有 uploadId，说明是断点续传，不能重新生成 uploadId
     * @param bucketName 存储桶名称
     * @param objectKey 对象名称
     * @param metadata 对象元数据
     * @return 初始化分片上传结果对象，包含uploadId等信息
     */
    InitiateMultipartUploadResult initMultipartUploadTask(String bucketName, String objectKey, ObjectMetadata metadata);


    /**
     * 2-生成分片上传地址，返回给前端
     * @param bucketName 存储桶名称
     * @param objectKey 对象名称
     * @param httpMethod HTTP方法，如GET、PUT等
     * @param expiration 签名过期时间
     * @param params 签名中包含的参数
     * @return 生成的预签名URL
     */
    URL genePreSignedUrl(String bucketName, String objectKey, HttpMethod httpMethod, Date expiration, Map<String,Object> params);

    /**
     * 3-合并分片
     * @param bucketName 存储桶名称
     * @param objectKey 对象名称
     * @param uploadId 分片上传ID
     * @param partETags 分片ETag列表，用于验证分片的完整性
     * @return 完成分片上传结果对象
     */
    CompleteMultipartUploadResult mergeChunks(String bucketName, String objectKey, String uploadId, List<PartETag> partETags);


}
