package net.xdclass.dto;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

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
@Schema(name = "AccountFileDO", description = "用户文件表")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountFileDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    private Long id;

    @Schema(description = "用户ID")
    private Long accountId;

    @Schema(description = "状态 0不是文件夹，1是文件夹")
    private Integer isDir;

    @Schema(description = "上层文件夹ID,顶层文件夹为0")
    private Long parentId;

    @Schema(description = "文件ID，真正存储的文件")
    private Long fileId;

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "文件类型：普通文件common 、压缩文件compress 、  excel  、 word  、 pdf  、 txt  、 图片img  、音频audio  、视频video 、ppt 、源码文件code  、 csv")
    private String fileType;

    @Schema(description = "文件的后缀拓展名")
    private String fileSuffix;

    @Schema(description = "文件大小，字节")
    private Long fileSize;

    @Schema(description = "逻辑删除（0未删除，1已删除）")
    private Boolean del;

    @Schema(description = "删除日期")
    private Date delTime;

    @Schema(description = "更新时间")
    private Date gmtModified;

    @Schema(description = "创建时间")
    private Date gmtCreate;
}
