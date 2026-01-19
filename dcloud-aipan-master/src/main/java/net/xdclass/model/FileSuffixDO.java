package net.xdclass.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 文件分类表
 * </p>
 *
 * @author 小滴课堂-二当家小D,
 * @since 2024-12-24
 */
@Getter
@Setter
@TableName("file_suffix")
@Schema(name = "FileSuffixDO", description = "文件分类表")
public class FileSuffixDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "文件扩展名")
    @TableField("file_suffix")
    private String fileSuffix;

    @Schema(description = "文件类型ID")
    @TableField("file_type_id")
    private Integer fileTypeId;
}
