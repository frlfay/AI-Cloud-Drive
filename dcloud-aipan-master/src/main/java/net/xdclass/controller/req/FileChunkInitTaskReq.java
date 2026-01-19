package net.xdclass.controller.req;

import lombok.Data;
import lombok.experimental.Accessors;

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
public class FileChunkInitTaskReq {

    private Long accountId;

    private String filename;

    private String identifier;

    /***
     * 总大小
     */
    private Long totalSize;

    /**
     * 分片大小
     */
    private Long chunkSize;

}