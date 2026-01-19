package net.xdclass.config;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    @Value("endpoint")
    private String endpoint;

    @Value("access-key")
    private String accessKey;

    @Value("access-secret")
    private String accessSecret;

    @Value("bucket-name")
    private String bucketName;

    @Value("avatar-bucket-name")
    private String avatarBucketName;

    //预签名的URL过期时间 ms 毫秒
    private Long preSignUrlExpireTime = 60 * 10 * 1000L;

//
//    @Bean
//    public MinioClient getMinIoClient(){
//        return MinioClient.builder().endpoint(endpoint).credentials(accessKey, accessSecret).build();
//    }


}