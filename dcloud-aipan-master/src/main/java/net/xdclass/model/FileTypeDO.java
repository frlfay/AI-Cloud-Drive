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
 * 文件类型表
 * </p>
 *
 * @author 小滴课堂-二当家小D,
 * @since 2024-12-24
 */
@Getter
@Setter
@TableName("file_type")
@Schema(name = "FileTypeDO", description = "文件类型表")
public class FileTypeDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "文件类型名")
    @TableField("file_type_name")
    private String fileTypeName;
}
