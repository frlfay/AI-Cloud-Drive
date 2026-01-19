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
 * 用户信息表
 * </p>
 *
 * @author 小滴课堂-二当家小D,
 * @since 2024-12-24
 */
@Getter
@Setter
@TableName("account")
@Schema(name = "AccountDO", description = "用户信息表")
public class AccountDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
      @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "用户名")
    @TableField("username")
    private String username;

    @Schema(description = "密码")
    @TableField("password")
    private String password;

    @Schema(description = "用户头像")
    @TableField("avatar_url")
    private String avatarUrl;

    @Schema(description = "手机号")
    @TableField("phone")
    private String phone;

    @Schema(description = "用户角色 COMMON, ADMIN")
    @TableField("role")
    private String role;

    @Schema(description = "逻辑删除（1删除 0未删除）")
    @TableField("del")
    @TableLogic
    private Boolean del;

    @Schema(description = "创建时间")
    @TableField("gmt_create")
    private Date gmtCreate;

    @Schema(description = "更新时间")
    @TableField("gmt_modified")
    private Date gmtModified;
}
