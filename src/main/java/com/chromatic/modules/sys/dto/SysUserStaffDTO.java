package com.chromatic.modules.sys.dto;

import com.chromatic.modules.sys.entity.SysRoleEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SysUserStaffDTO {
    private static final long serialVersionUID = 1L;
    /**
     * 备注
     */
    @ApiModelProperty(value = "人员id")
    private Long staffId;

    @ApiModelProperty(value = "关联人员")
    private String staffName;

    @ApiModelProperty(value = "人员code")
    private String staffCode;


    /**
     * 备注
     */
    @ApiModelProperty(value = "角色")
    private List<SysRoleEntity> roles;

}
