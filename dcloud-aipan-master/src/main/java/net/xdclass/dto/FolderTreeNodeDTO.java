package net.xdclass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
public class FolderTreeNodeDTO {

    /**
     * 文件id
     */
    private Long id ;

    /**
     * 父文件ID
     */
    private Long parentId;


    /**
     * 文件名称
     */
    private String label;

    /**
     * 子节点列表
     */
    private List<FolderTreeNodeDTO> children = new ArrayList<>();

}