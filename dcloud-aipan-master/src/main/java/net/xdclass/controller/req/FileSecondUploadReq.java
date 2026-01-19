package net.xdclass.controller.req;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/
@Data
@Accessors(chain = true)
public class FileSecondUploadReq {

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件唯一标识（md5）
     */
    private String identifier;

    /**
     * 用户id
     */
    private Long accountId;

    /**
     * 父级目录id
     */
    private Long parentId;

    /**
     * 文件大小
     */
    //private Long fileSize;



}