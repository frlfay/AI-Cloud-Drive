package net.xdclass.dto;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

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
@Schema(name = "AccountDO", description = "用户信息")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO implements Serializable {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;


    @Schema(description = "用户头像")
    private String avatarUrl;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "用户角色 COMMON, ADMIN")
    private String role;

    @Schema(description = "逻辑删除（1删除 0未删除）")
    private Boolean del;

    @Schema(description = "创建时间")
    private Date gmtCreate;

    @Schema(description = "更新时间")
    private Date gmtModified;


    /**
     * 根文件夹ID
     */
    private Long rootFileId;
    /**
     * 根文件夹名称
     */
    private String rootFileName;

    /**
     * 存储信息
     */
    private StorageDTO storageDTO;
}
