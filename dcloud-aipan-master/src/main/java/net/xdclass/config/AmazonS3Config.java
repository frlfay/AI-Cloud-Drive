package net.xdclass.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.annotation.Beta;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
@Configuration
public class AmazonS3Config {


    /**
     * minio 配置类
     */
    @Resource
    private MinioConfig minioConfig;


    /**
     * 创建并配置 s3 客户端
     * @return
     */
    @Bean(name = "amazonS3Client")
    public AmazonS3 getClient() {

        // 创建客户端配置
        ClientConfiguration config = new ClientConfiguration();

        //配置协议
        config.setProtocol(Protocol.HTTP);

        //配置超时时间
        config.setConnectionTimeout(5000);
        config.setUseExpectContinue(true);

        //使用minio配置的密钥创建访问凭据
        AWSCredentials awsCredentials = new BasicAWSCredentials(
                minioConfig.getAccessKey(),
                minioConfig.getAccessSecret()
        );

        //设置Endpoint
        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
                minioConfig.getEndpoint(),
                Regions.US_EAST_1.name()
        );

        // 使用以上配置创建并返回Amazon S3客户端实例
        return AmazonS3ClientBuilder.standard()
                .withClientConfiguration(config)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(endpointConfiguration)
                .withPathStyleAccessEnabled(true).build();


    }

}