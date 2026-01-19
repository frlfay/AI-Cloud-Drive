package net.xdclass.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
@Data
@Builder
@Schema(description = "用户登录请求对象")
public class AccountLoginReq {

    /**
     * 密码
     */
    @Schema(description = "用户密码", required = true, example = "123456")
    private String password;

    /**
     * 手机号
     */
    @Schema(description = "用户手机号", required = true, example = "13800138000")
    private String phone;

}
