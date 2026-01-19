package net.xdclass.controller.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShareCreateReq {

    /**
     * 分享名称
     */
    private String shareName;


    /**
     * 分享类型，是否需要提取码
     */
    private String shareType;

    /**
     * 分享有效天数0永久，1-7天，2-30天
     */
    private Integer shareDayType;

    /**
     * 文件id列表
     */
    private List<Long> fileIds;

    /**
     * 分享人id
     */
    private Long accountId;

}