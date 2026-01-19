package net.xdclass.controller.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@AllArgsConstructor
@NoArgsConstructor
public class FolderCreateReq {

    /**
     * 文件夹名称
     */
    private String folderName;

    /**
     * 上级文件夹ID
     */
    private Long parentId;

    /**
     * 用户ID
     */
    private Long accountId;
}