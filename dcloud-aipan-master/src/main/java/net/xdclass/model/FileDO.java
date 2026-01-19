package net.xdclass.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户文件表
 * </p>
 *
 * @author 小滴课堂-二当家小D,
 * @since 2024-12-24
 */
@Getter
@Setter
@TableName("file")
@Schema(name = "FileDO", description = "用户文件表")
public class FileDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "文件id")
      @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "用户id，是哪个用户初次上传的")
    @TableField("account_id")
    private Long accountId;

    @Schema(description = "文件名称，秒传需要用到，冗余存储")
    @TableField("file_name")
    private String fileName;

    @Schema(description = "文件的后缀拓展名，冗余存储")
    @TableField("file_suffix")
    private String fileSuffix;

    @Schema(description = "文件大小，字节，冗余存储")
    @TableField("file_size")
    private Long fileSize;

    @Schema(description = "文件的key, 格式 日期/md5.拓展名，比如 2024-11-13/921674fd-cdaf-459a-be7b-109469e7050d.png")
    @TableField("object_key")
    private String objectKey;

    @Schema(description = "唯一标识，文件MD5")
    @TableField("identifier")
    private String identifier;

    @Schema(description = "逻辑删除（0未删除，1已删除）")
    @TableField("del")
    @TableLogic
    private Boolean del;

    @Schema(description = "更新时间")
    @TableField("gmt_modified")
    private Date gmtModified;

    @Schema(description = "创建时间")
    @TableField("gmt_create")
    private Date gmtCreate;
}
