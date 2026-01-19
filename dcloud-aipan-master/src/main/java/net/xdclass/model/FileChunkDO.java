package net.xdclass.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文件分片信息表
 * </p>
 *
 * @author 小滴课堂-二当家小D,
 * @since 2024-12-24
 */
@Getter
@Setter
@TableName("file_chunk")
@Schema(name = "FileChunkDO", description = "文件分片信息表")
@Accessors(chain = true)
public class FileChunkDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "文件唯一标识（md5）")
    @TableField("identifier")
    private String identifier;

    @Schema(description = "分片上传ID")
    @TableField("upload_id")
    private String uploadId;

    @Schema(description = "文件名")
    @TableField("file_name")
    private String fileName;

    @Schema(description = "所属桶名")
    @TableField("bucket_name")
    private String bucketName;

    @Schema(description = "文件的key")
    @TableField("object_key")
    private String objectKey;

    @Schema(description = "总文件大小（byte）")
    @TableField("total_size")
    private Long totalSize;

    @Schema(description = "每个分片大小（byte）")
    @TableField("chunk_size")
    private Long chunkSize;

    @Schema(description = "分片数量")
    @TableField("chunk_num")
    private Integer chunkNum;

    @Schema(description = "用户ID")
    @TableField("account_id")
    private Long accountId;

    @TableField("gmt_create")
    private Date gmtCreate;

    @TableField("gmt_modified")
    private Date gmtModified;
}
