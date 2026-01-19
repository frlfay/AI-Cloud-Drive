package net.xdclass.dto;

import com.amazonaws.services.s3.model.PartSummary;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.xdclass.model.FileChunkDO;
import net.xdclass.util.SpringBeanUtil;

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
@NoArgsConstructor
@Accessors(chain = true)
public class FileChunkDTO {


    public  FileChunkDTO(FileChunkDO fileChunkDO){
        SpringBeanUtil.copyProperties(fileChunkDO,this);
    }


    private Long id;

    @Schema(description = "文件唯一标识（md5）")
    private String identifier;

    @Schema(description = "分片上传ID")
    private String uploadId;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "所属桶名")
    private String bucketName;

    @Schema(description = "文件的key")
    private String objectKey;

    @Schema(description = "总文件大小（byte）")
    private Long totalSize;

    @Schema(description = "每个分片大小（byte）")
    private Long chunkSize;

    @Schema(description = "分片数量")
    private Integer chunkNum;

    @Schema(description = "用户ID")
    private Long accountId;


    /**
     * 是否完成上传
     */
    private boolean finished;

    /**
     * 返回已经存在的分片
     */
    private List<PartSummary> exitPartList;

}