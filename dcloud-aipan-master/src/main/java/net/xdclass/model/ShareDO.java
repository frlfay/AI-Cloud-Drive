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
 * 用户分享表
 * </p>
 *
 * @author 小滴课堂-二当家小D,
 * @since 2024-12-24
 */
@Getter
@Setter
@TableName("share")
@Schema(name = "ShareDO", description = "用户分享表")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShareDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "分享id")
      @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "分享名称")
    @TableField("share_name")
    private String shareName;

    @Schema(description = "分享类型（no_code没有提取码 ,need_code有提取码）")
    @TableField("share_type")
    private String shareType;

    @Schema(description = "分享类型（0 永久有效；1: 7天有效；2: 30天有效）")
    @TableField("share_day_type")
    private Integer shareDayType;

    @Schema(description = "分享有效天数（永久有效为0）")
    @TableField("share_day")
    private Integer shareDay;

    @Schema(description = "分享结束时间")
    @TableField("share_end_time")
    private Date shareEndTime;

    @Schema(description = "分享链接地址")
    @TableField("share_url")
    private String shareUrl;

    @Schema(description = "分享提取码")
    @TableField("share_code")
    private String shareCode;

    @Schema(description = "分享状态  used正常, expired已失效,  cancled取消")
    @TableField("share_status")
    private String shareStatus;

    @Schema(description = "分享创建人")
    @TableField("account_id")
    private Long accountId;

    @Schema(description = "创建时间")
    @TableField("gmt_create")
    private Date gmtCreate;

    @TableField("gmt_modified")
    private Date gmtModified;
}
