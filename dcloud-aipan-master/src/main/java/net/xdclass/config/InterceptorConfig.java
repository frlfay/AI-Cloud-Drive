package net.xdclass.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
@Configuration
@Slf4j
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                //添加拦截的路径
                .addPathPatterns("/api/account/*/**","/api/file/*/**","/api/share/*/**","/api/recycle/*/**")

                //排除不拦截
                .excludePathPatterns("/api/account/*/register","/api/account/*/login","/api/account/*/upload_avatar",
                        "/api/share/*/check_share_code","/api/share/*/visit","/api/share/*/detail_no_code","/api/share/*/detail_with_code");
    }

}