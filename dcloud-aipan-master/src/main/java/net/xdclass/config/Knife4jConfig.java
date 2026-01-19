package net.xdclass.config;



import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Configuration
public class Knife4jConfig {


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("小滴课堂AI网盘系统 API")
                        .version("1.0")
                        .description("AI网盘系统")
                        .termsOfService("https://xdclass.net")
                        .license(new License().name("Apache 2.0").url("https://xdclass.net"))
                        // 添加作者信息
                        .contact(new Contact()
                                .name("二当家小D") // 替换为作者的名字
                                .email("794666918@.com") // 替换为作者的电子邮件
                                .url("https://xdclass.net") // 替换为作者的网站或个人资料链接
                        )
                ) ;
    }

}