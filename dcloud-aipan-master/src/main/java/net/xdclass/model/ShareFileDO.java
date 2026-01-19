package net.xdclass.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文件分享表
 * </p>
 *
 * @author 小滴课堂-二当家小D,
 * @since 2024-12-24
 */
@Getter
@Setter
@TableName("share_file")
@Schema(name = "ShareFileDO", description = "文件分享表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareFileDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
      @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "分享id")
    @TableField("share_id")
    private Long shareId;

    @Schema(description = "用户文件的ID")
    @TableField("account_file_id")
    private Long accountFileId;

    @Schema(description = "创建者id")
    @TableField("account_id")
    private Long accountId;

    @Schema(description = "分享时间")
    @TableField("gmt_create")
    private Date gmtCreate;

    @Schema(description = "更新时间")
    @TableField("gmt_modified")
    private Date gmtModified;
}
