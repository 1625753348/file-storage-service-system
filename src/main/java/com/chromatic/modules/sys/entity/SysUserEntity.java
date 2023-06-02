package com.chromatic.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 系统用户
 *
 * @author Seven ME info@7-me.net
 * @since v1.0.0 2022-08-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
public class SysUserEntity {

    /**
     *
     */
    @TableField(value = "`id`")
    @TableId(value = "`id`", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户名
     */
    @TableField(value = "`name`")
    private String name;


    @TableField(value = "`is_user_password`")
    private Integer isUserPassword;


    /**
     * 账号过期时间
     */
    @TableField(value = "`expiration_time`")
    private Date expirationTime;


    /**
     * 密码
     */
    @TableField(value = "`password`", updateStrategy = FieldStrategy.NOT_NULL)
    private String password;

    /**
     * 盐
     */
    @TableField(value = "`salt`", updateStrategy = FieldStrategy.NOT_NULL)
    private String salt;

    /**
     * 状态  0：禁用   1：正常
     */
    @TableField(value = "`status`")
    private Integer status;

    /**
     * 建立时间
     */
    @TableField(value = "`create_time`", updateStrategy = FieldStrategy.NOT_NULL)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "`update_time`", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 创建人
     */
    @TableField(value = "`create_user`", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_NULL)
    private String createUser;

    /**
     * 更新人
     */
    @TableField(value = "`update_user`", fill = FieldFill.INSERT_UPDATE)
    private String updateUser;

    /**
     * 删除标志
     */
    @TableField(value = "`del_flag`")
    @TableLogic(value = "0", delval = "NULL")
    private Integer delFlag;

    /**
     * 角色ID列表
     */
    @TableField(exist = false)
    private List<Long> roleIdList;

    @TableField(exist = false)
    private List<SysRoleEntity> roleList;

    @TableField(exist = false)
    private List<SysMenuEntity> menuList;

    @TableField(exist = false)
    private List<String> urlList;

    /**
     * 创建者ID
     */
    @TableField(value = "`create_user_id`", fill = FieldFill.INSERT)
    private Long createUserId;

}