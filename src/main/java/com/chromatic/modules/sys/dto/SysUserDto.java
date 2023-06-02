package com.chromatic.modules.sys.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chromatic.modules.sys.entity.SysRoleEntity;
import com.chromatic.common.validator.group.AddGroup;
import com.chromatic.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/************************************************************************
 * @author: wg
 * @description:
 * @createTime: 13:31 2022/3/23
 * @updateTime: 13:31 2022/3/23
 ************************************************************************/
@Data
public class SysUserDto {

    /**
     * 用户ID
     */
    @TableId
    private Long id;

    @TableField(value = "`isUserPassword`")
    private Integer isUserPassword;

    @TableField(value = "`expirationTime`")
    private Date expirationTime;
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String name;

    /**
     * 角色ID列表
     */
    @TableField(exist = false)
    private List<Long> roleIdList;

    @TableField(exist = false)
    private List<SysRoleEntity> roleList;

    @TableField(value = "`status`")
    private Integer status;

    /**
     * 创建者ID
     */
    private Long createUserId;

    /**
     * 创建时间
     */
    private Date createTime;


}
