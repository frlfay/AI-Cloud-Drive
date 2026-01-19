package net.xdclass.controller.req;

import lombok.Data;

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
public class FileBatchReq {

    /**
     * 文件id列表
     */
    private List<Long> fileIds;

    /**
     * 目标父级id
     */
    private Long targetParentId;

    /**
     * 用户id
     */
    private Long accountId;
}