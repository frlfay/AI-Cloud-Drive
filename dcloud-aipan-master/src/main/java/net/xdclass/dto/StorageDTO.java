package net.xdclass.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 存储信息表
 * </p>
 *
 * @author 小滴课堂-二当家小D,
 * @since 2024-12-24
 */
@Getter
@Setter
@Schema(name = "StorageDO", description = "存储信息表")
public class StorageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @Schema(description = "所属用户")
    private Long accountId;

    @Schema(description = "占用存储大小")

    private Long usedSize;

    @Schema(description = "总容量大小，字节存储")

    private Long totalSize;

    @Schema(description = "创建时间")
    private Date gmtCreate;

    @Schema(description = "更新时间")
    private Date gmtModified;
}
