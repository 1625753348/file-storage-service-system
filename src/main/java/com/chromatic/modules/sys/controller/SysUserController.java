/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.modules.sys.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chromatic.common.annotation.sys.SysLog;
import com.chromatic.common.exception.SevenmeException;
import com.chromatic.common.utils.PageUtils;
import com.chromatic.common.validator.AssertUtils;
import com.chromatic.common.validator.ValidatorUtils;
import com.chromatic.common.validator.group.AddGroup;
import com.chromatic.common.validator.group.UpdateGroup;
import com.chromatic.common.vo.Result;

import com.chromatic.modules.sys.convert.SysInfoConvert;
import com.chromatic.modules.sys.dao.SysUserDao;
import com.chromatic.modules.sys.dto.SysInfoDTO;
import com.chromatic.modules.sys.dto.SysUserDto;
import com.chromatic.modules.sys.dto.SysUserStaffDTO;
import com.chromatic.modules.sys.entity.SysMenuEntity;
import com.chromatic.modules.sys.entity.SysUserEntity;
import com.chromatic.modules.sys.form.PasswordForm;
import com.chromatic.modules.sys.service.SysUserRoleService;
import com.chromatic.modules.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统用户
 *
 * @author chromatic
 */
@RestController
@RequestMapping("/sys/user")
@Api(tags = "系统用户管理")
public class SysUserController extends AbstractController {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 所有用户列表
     */
    @GetMapping("/page")
    // @RequiresPermissions(value = "sys:user:list")
    @ApiOperation("所有用户列表")
    public Result page(@ApiIgnore @RequestParam Map<String, Object> params) {
        //只有超级管理员，才能查看所有管理员列表
        if (sysUserRoleService.isSuper(getUserId())) {
            params.put("createUserId", getUserId());
        }
        PageUtils page = sysUserService.queryPage(params);

        Result<Object> result = new Result<>();
        result.setData(page);
        return result.ok();
    }

    @GetMapping("/list")
    // @RequiresPermissions(value = "sys:user:list")
    @ApiOperation("所有用户列表, 能获取其角色列表")
    public Result list(@ApiIgnore @RequestParam Map<String, Object> params) {

        return getAllUsers(params);
    }

    @GetMapping("/getallusers")
    // @RequiresPermissions(value = "sys:user:list")
    @ApiOperation("所有用户列表, 能获取其角色列表")
    public Result<List<SysUserDto>> getAllUsers(@ApiIgnore @RequestParam Map<String, Object> params) {
        //只有超级管理员，才能查看所有管理员列表
        Boolean f = sysUserRoleService.isSuper(getUserId());
        if (sysUserRoleService.isSuper(getUserId())) {
            params.put("createUserId", getUserId());
        }
        List<SysUserEntity> allUsers = sysUserService.getAllUsers(params).stream()
                .filter(x->!x.getName().equals("root"))
                .filter(x->!x.getName().equals("admin")).collect(Collectors.toList());

        Result<List<SysUserDto>> result = new Result<>();

        List<SysUserDto> userDtos = new ArrayList<>();
        SysUserDto userDto = null;
        for (SysUserEntity user : allUsers) {
            userDto = new SysUserDto();
            BeanUtil.copyProperties(user, userDto);
            userDtos.add(userDto);
        }

        result.setData(userDtos);
        return result.ok();
    }

    /**
     * 获取登录的用户信息
     */
    @GetMapping("/info")
    @ApiOperation("获取登录的用户信息")
    public Result info() {
        Result<Object> result = new Result<>();
        SysUserEntity user = getUser();
        SysInfoDTO info = SysInfoConvert.INSTANCE.EntityToDTO(user);
        SysUserStaffDTO data = sysUserService.getInfoByCurrentId();
        info.setStaffName(data.getStaffName());
        info.setStaffCode(data.getStaffCode());

        result.setData(info);
        return result.ok();
    }

    @SysLog("修改状态")
    @PostMapping("/setRoleStatus")
    @ApiOperation("修改登录用户密码")
    public Result setRoleStatus(@PathVariable("userId") Long userId, @PathVariable("status") String status) {
        Result<Object> result = new Result<>();

        //更新密码
        boolean flag = sysUserService.updateStatus(userId, status);

        return result.ok();
    }


    /**
     * 修改登录用户密码
     */
    @SysLog("修改密码")
    @PostMapping("/password")
    @ApiOperation("修改登录用户密码")
    public Result password(@RequestBody PasswordForm form) {
        Result<Object> result = new Result<>();
        AssertUtils.isBlank(form.getNewPassword(), "新密码不为能空");

        //sha256加密
        String password = new Sha256Hash(form.getPassword(), getUser().getSalt()).toHex();
        //sha256加密
        String newPassword = new Sha256Hash(form.getNewPassword(), getUser().getSalt()).toHex();

        //更新密码
        boolean flag = sysUserService.updatePassword(getUserId(), password, newPassword);
        if (!flag) {
            return result.error("原密码不正确");
        }

        return result.ok();
    }

    /**
     * 修改系统用户密码
     */
    @SysLog("修改用户密码")
    @PostMapping("/syspassword")
    @ApiOperation("修改登录用户密码")
    public Result sysPassword(@RequestBody PasswordForm form) {
        Result<Object> result = new Result<>();
        AssertUtils.isBlank(form.getNewPassword(), "新密码不为能空");
        Long sysUserId = form.getSysUserId();
        SysUserEntity sysUser = getSysUser(sysUserId);
        List<SysMenuEntity> menuList = getUser().getMenuList();
        List<String> collect = menuList.stream().map(SysMenuEntity::getUrl).collect(Collectors.toList());
        List<String> collect1 = collect.stream().filter(x -> x.equals("/system/user")).collect(Collectors.toList());
        //校验是否有菜单权限
        AssertUtils.isListEmpty(collect1, "没有菜单权限!");

        //sha256加密
        String newPassword = new Sha256Hash(form.getNewPassword(),sysUser.getSalt()).toHex();

        //更新密码
        boolean flag = sysUserService.updateSysPassword(sysUserId, newPassword);
        if (!flag) {
            return result.error("不正确");
        }

        return result.ok();
    }
    /**
     * 用户信息
     */
    @GetMapping("/getbyid/{userId}")
    // @RequiresPermissions(value = "sys:user:info")
    @ApiOperation("用户信息, sys:user:info")
    public Result getById(@PathVariable("userId") Long userId) {
        SysUserEntity user = sysUserService.getById(userId);

        //获取用户所属的角色列表
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userId);
        user.setRoleIdList(roleIdList);

        Result<Object> result = new Result<>();
        result.setData(user);
        return result.ok();
    }

    /**
     * 保存用户
     */
    @SysLog("保存用户")
    @PostMapping("/add")
    // @RequiresPermissions(value = "sys:user:save")
    @ApiOperation("保存用户, sys:user:save")
    public Result add(@RequestBody SysUserEntity user) {
        ValidatorUtils.validateEntity(user, AddGroup.class);

        user.setCreateUserId(getUserId());
        sysUserService.saveUser(user);

        Result<Object> result = new Result<>();
        return result.ok();
    }

    /**
     * 修改用户
     */
    @SysLog("修改用户")
    @PostMapping("/update")
    // @RequiresPermissions(value = "sys:user:update")
    @ApiOperation("修改用户, sys:user:update")
    public Result update(@RequestBody SysUserEntity user) {
        ValidatorUtils.validateEntity(user, UpdateGroup.class);

        user.setCreateUserId(getUserId());
        sysUserService.update(user);

        Result<Object> result = new Result<>();
        return result.ok();
    }

    /**
     * 删除用户
     */
    @SysLog("删除用户")
    @PostMapping("/remove")
    // @RequiresPermissions(value = "sys:user:delete")
    @ApiOperation("删除用户, sys:user:delete")
    @ApiImplicitParams({})
    public Result remove(@RequestBody Long[] userIds) {
        Result<Object> result = new Result<>();

        List<SysUserEntity> sysUserEntities = sysUserDao.selectList(
                new QueryWrapper<SysUserEntity>().lambda().in(SysUserEntity::getId, userIds));
        List<String> nameCollect = sysUserEntities.stream().map(SysUserEntity::getName).collect(Collectors.toList());
        if (nameCollect.contains( "admin") || nameCollect.contains( "root")) {
            return result.error("系统管理员不能删除");
        }
        if (ArrayUtils.contains(userIds, getUserId())) {
            return result.error("当前用户不能删除, 自己不能删除自己");
        }

        sysUserService.deleteBatch(userIds);

        return result.ok();
    }


    /**
     * 当前人员信息
     */
    @PostMapping("/currentuser")
    @ApiOperation("用户信息, sys:user:info")
    public Result<SysUserStaffDTO> getInfoById(@ApiIgnore @RequestParam Map<String, Object> params) {
        Result<SysUserStaffDTO> result = new Result<>();
        try {
            SysUserStaffDTO data = sysUserService.getInfoByCurrentId();

            return result.ok(data);
        } catch (SevenmeException e) {
            e.printStackTrace();
            return result.error(e.getMessage());
        }
    }

    /**
     * 重置密码
     * @param id
     * @return
     */
    @PostMapping(value = "resetpassword/{id}")
    @ApiOperation("重置密码")
    public Result<Object> reset(@PathVariable String id) {
        if (sysUserService.resetPassword(id) == 1) {
            return new Result<>().ok(null);
        } else {
            return new Result<>().error();
        }
    }

}
