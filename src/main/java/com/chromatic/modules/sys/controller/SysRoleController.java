/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.modules.sys.controller;

import com.chromatic.common.annotation.sys.SysLog;
import com.chromatic.common.utils.PageUtils;
import com.chromatic.common.validator.ValidatorUtils;
import com.chromatic.common.vo.Result;
import com.chromatic.modules.sys.entity.SysRoleEntity;
import com.chromatic.modules.sys.service.SysRoleMenuService;
import com.chromatic.modules.sys.service.SysRoleService;
import com.chromatic.modules.sys.service.SysUserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理
 *
 * @author Mark chromatic
 */
@RestController
@RequestMapping("/sys/role")
@Api(tags = "角色管理")
public class SysRoleController extends AbstractController {
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysUserRoleService sysUserRoleService;

    @GetMapping("/page")
    // @RequiresPermissions(value = "sys:role:list")
    @ApiOperation("角色列表, sys:role:list")
    public Result page(@ApiIgnore @RequestParam Map<String, Object> params) {
        //如果不是超级管理员，则只查询自己创建的角色列表
        if (sysUserRoleService.isSuper(getUserId())){
            params.put("createUserId", getUserId());
        }

        PageUtils page = sysRoleService.queryPage(params);

        Result<Object> result = new Result<>();
        result.setData(page);
        return result.ok();
    }

    /**
     * 角色列表
     */
    @GetMapping("/list")
    // @RequiresPermissions(value = "sys:role:list")
    @ApiOperation("角色列表, sys:role:list")
    public Result list(@ApiIgnore @RequestParam Map<String, Object> params) {
        //如果不是超级管理员，则只查询自己创建的角色列表
//        if (sysUserRoleService.isSuper(getUserId())){
//            params.put("createUserId", getUserId());
//        }

        Result<Object> result = new Result<>();

        List<SysRoleEntity> list = sysRoleService.list(params);
        result.setData(list);

        return result.ok();
    }

    /**
     * 角色列表
     */
    @GetMapping("/select")
    // @RequiresPermissions(value = "sys:role:select")
    @ApiOperation("角色列表, sys:role:select")
    public Result select() {
        Map<String, Object> map = new HashMap<>();

        //如果不是超级管理员，则只查询自己所拥有的角色列表
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(getUserId());
        if (sysUserRoleService.isSuper(getUserId()) && !roleIdList.contains(0L)) {
            map.put("create_user_id", getUserId());
        }
        List<SysRoleEntity> list = (List<SysRoleEntity>) sysRoleService.listByMap(map);

        Result<Object> result = new Result<>();
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("list", list);
        result.setData(hashMap);
        return result.ok();
    }

    /**
     * 角色信息
     */
    @GetMapping("/info/{roleId}")
    // @RequiresPermissions(value = "sys:role:info")
    @ApiOperation("角色信息, sys:role:info")
    public Result info(@PathVariable("roleId") Long roleId) {
        SysRoleEntity role = sysRoleService.getById(roleId);

        //查询角色对应的菜单
        List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(roleId);
        role.setMenuIdList(menuIdList);

        Result<Object> result = new Result<>();
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("role", role);
        result.setData(hashMap);
        return result.ok();
    }

    /**
     * 保存角色
     */
    @SysLog("保存角色")
    @PostMapping("/add")
    // @RequiresPermissions(value = "sys:role:save")
    @ApiOperation("保存角色, sys:role:save")
    public Result add(@RequestBody SysRoleEntity role) {
        ValidatorUtils.validateEntity(role);

        role.setCreateUserId(getUserId());
        sysRoleService.saveRole(role);

        Result<Object> result = new Result<>();
        return result.ok();
    }

    /**
     * 修改角色
     */
    @SysLog("修改角色")
    @PostMapping("/update")
    // @RequiresPermissions(value = "sys:role:update")
    @ApiOperation("修改角色, sys:role:update")
    public Result update(@RequestBody SysRoleEntity role) {
        ValidatorUtils.validateEntity(role);

        role.setCreateUserId(getUserId());

        sysRoleService.update(role);

        Result<Object> result = new Result<>();
        return result.ok();
    }

    /**
     * 删除角色
     */
    @SysLog("删除角色")
    @PostMapping("/remove")
    // @RequiresPermissions(value = "sys:role:delete")
    @ApiOperation("删除角色, sys:role:delete")
    public Result remove(@RequestBody Long[] roleIds) {
        sysRoleService.deleteBatch(roleIds);

        Result<Object> result = new Result<>();
        return result.ok();
    }
}
