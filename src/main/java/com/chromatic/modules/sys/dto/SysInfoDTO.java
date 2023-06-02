package com.chromatic.modules.sys.dto;

import com.chromatic.modules.sys.entity.SysMenuEntity;
import com.chromatic.modules.sys.entity.SysRoleEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "角色dto")
public class SysInfoDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "角色ID")
    private Long id;


    @ApiModelProperty(value = "用户修改过密码为1,默认新建或重置密码后为0")
    private Integer isUserPassword;

    @ApiModelProperty(value = "关联人员")
    private String staffName;

    @ApiModelProperty(value = "人员code")
    private String staffCode;



    /**
     * 用户名
     */
    @ApiModelProperty(value = "角色名称")
    private String name;


    /**
     * 状态  0：禁用   1：正常
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * 角色ID列表
     */
    @ApiModelProperty(value = "关联角色id列表")
    private List<Long> roleIdList;

    @ApiModelProperty(value = "关联角色名列表")
    private List<SysRoleEntity> roleList;

    @ApiModelProperty(value = "关联菜单列表")
    private List<SysMenuEntity> menuList;

    @ApiModelProperty(value = "角色名称")
    private List<String> urlList;


}
