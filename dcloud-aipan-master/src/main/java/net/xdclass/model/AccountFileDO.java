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
@TableName("account_file")
@Schema(name = "AccountFileDO", description = "用户文件表")
public class AccountFileDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
      @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "用户ID")
    @TableField("account_id")
    private Long accountId;

    @Schema(description = "状态 0不是文件夹，1是文件夹")
    @TableField("is_dir")
    private Integer isDir;

    @Schema(description = "上层文件夹ID,顶层文件夹为0")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "文件ID，真正存储的文件")
    @TableField("file_id")
    private Long fileId;

    @Schema(description = "文件名称")
    @TableField("file_name")
    private String fileName;

    @Schema(description = "文件类型：普通文件common 、压缩文件compress 、  excel  、 word  、 pdf  、 txt  、 图片img  、音频audio  、视频video 、ppt 、源码文件code  、 csv")
    @TableField("file_type")
    private String fileType;

    @Schema(description = "文件的后缀拓展名")
    @TableField("file_suffix")
    private String fileSuffix;

    @Schema(description = "文件大小，字节")
    @TableField("file_size")
    private Long fileSize;

    @Schema(description = "逻辑删除（0未删除，1已删除）")
    @TableField("del")
    @TableLogic
    private Boolean del;

    @Schema(description = "删除日期")
    @TableField("del_time")
    private Date delTime;

    @Schema(description = "更新时间")
    @TableField("gmt_modified")
    private Date gmtModified;

    @Schema(description = "创建时间")
    @TableField("gmt_create")
    private Date gmtCreate;
}
