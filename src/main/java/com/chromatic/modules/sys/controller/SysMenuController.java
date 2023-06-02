/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.modules.sys.controller;

import com.chromatic.common.annotation.sys.SysLog;
import com.chromatic.common.exception.SevenmeException;
import com.chromatic.common.utils.Constant;
import com.chromatic.common.vo.Result;
import com.chromatic.modules.sys.entity.SysMenuEntity;
import com.chromatic.modules.sys.service.ShiroService;
import com.chromatic.modules.sys.service.SysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统菜单
 *
 * @author chromatic
 */
@RestController
@RequestMapping("/sys/menu")
@Api(tags = "系统菜单")
public class SysMenuController extends AbstractController {
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private ShiroService shiroService;

    /**
     * 导航菜单
     */
    @GetMapping("/nav")
    @ApiOperation("导航菜单")
    public Result nav() {
        List<SysMenuEntity> menuList = sysMenuService.getUserMenuList(getUserId());
        Set<String> permissions = shiroService.getUserPermissions(getUserId());

        Result<Object> result = new Result<>();
        Map<Object, Object> hashMap = new HashMap<>();
        hashMap.put("menuList", menuList);
        hashMap.put("permissions", permissions);
        result.setData(hashMap);
        return result.ok();
    }

    /**
     * 所有菜单列表
     */
    @GetMapping("/list")
    // @RequiresPermissions(value = "sys:menu:list")
    @ApiOperation("所有菜单列表")
    public List<SysMenuEntity> list() {
        List<SysMenuEntity> menuList = sysMenuService.list();
        Map<Long, SysMenuEntity> menuMap = new HashMap<>(12);
        for (SysMenuEntity s : menuList) {
            menuMap.put(s.getId(), s);
        }
        for (SysMenuEntity s : menuList) {
            SysMenuEntity parent = menuMap.get(s.getParentId());
            if (Objects.nonNull(parent)) {
                s.setParentName(parent.getName());
            }

        }

        return menuList;
    }

    /************************************************************************
     * @author: wg
     * @description: 根据登录人员获取菜单
     * @params:
     * @return:
     * @createTime: 17:41  2022/3/21
     * @updateTime: 17:41  2022/3/21
     ************************************************************************/
    @GetMapping(value = "/getmenulistbycurrentrole")
    // @RequiresPermissions(value = "sys:menu:list")
    @ApiOperation("根据登录人员获取菜单列表")
    public Result<Object> getMenuListByCurrentRole() {
        Long userId = getUserId();
        List<SysMenuEntity> userMenuList = sysMenuService.getMenus(userId);
        Set<String> permissions = shiroService.getUserPermissions(getUserId());

        Result<Object> result = new Result<>();
        Map<Object, Object> hashMap = new HashMap<>();
        hashMap.put("menuList", userMenuList);
        hashMap.put("permissions", permissions);
        result.setData(hashMap);
        return result.ok();
    }

    @GetMapping(value = "/getmenulistbyroleid")
    // @RequiresPermissions(value = "sys:menu:list")
    @ApiOperation("根据角色id获取菜单列表")
    public Result<Object> getMenuListByRoleId(Long[] roleIds) {

        Result<Object> result = new Result<>();
        ArrayList<Map<String, Object>> list = new ArrayList<>();

        for (Long roleId : roleIds) {
            List<SysMenuEntity> userMenuList;
            Set<String> permissions;
            Map<String, Object> hashMap = new HashMap<>();

            userMenuList = sysMenuService.getMenusByRoleId(roleId);
            // Set<String> permissions = shiroService.getUserPermissions(getUserId());
            permissions = userMenuList.stream().map(entity -> entity.getPerms()).collect(Collectors.toSet());

            hashMap.put("roleId", roleId);
            hashMap.put("menuList", userMenuList);
            hashMap.put("permissions", permissions);
            list.add(hashMap);
        }
        result.setData(list);
        return result.ok();
    }

    /**
     * 选择菜单(添加、修改菜单)
     */
    @GetMapping("/select")
    // @RequiresPermissions(value = "sys:menu:select")
    @ApiOperation("选择菜单")
    public Result select() {
        //查询列表数据
        List<SysMenuEntity> menuList = sysMenuService.queryNotButtonList();

        //添加顶级菜单
        SysMenuEntity root = new SysMenuEntity();
        root.setId(0L);
        root.setName("一级菜单");
        root.setParentId(-1L);
        root.setOpen(true);
        menuList.add(root);

        Result<Object> result = new Result<>();
        result.setData(menuList);
        return result.ok();
    }

    /**
     * 菜单信息
     */
    @GetMapping("/info/{menuId}")
    // @RequiresPermissions(value = "sys:menu:info")
    @ApiOperation("菜单信息")
    public Result info(@PathVariable("menuId") Long menuId) {
        SysMenuEntity menu = sysMenuService.getById(menuId);
        Result<Object> result = new Result<>();
        result.setData(menu);
        return result.ok();
    }

    /**
     * 保存
     */
    @SysLog("保存菜单")
    @PostMapping("/add")
    // @RequiresPermissions(value = "sys:menu:save")
    @ApiOperation("保存菜单")
    public Result add(@RequestBody SysMenuEntity menu) {
        //数据校验
        verifyForm(menu);

        sysMenuService.save(menu);

        Result<Object> result = new Result<>();
        return result.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改菜单")
    @PostMapping("/update")
    // @RequiresPermissions(value = "sys:menu:update")
    @ApiOperation("修改菜单")
    public Result update(@RequestBody SysMenuEntity menu) {
        //数据校验
        verifyForm(menu);

        sysMenuService.updateById(menu);

        Result<Object> result = new Result<>();
        return result.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除菜单")
    @PostMapping("/delete/{menuId}")
    // @RequiresPermissions(value = "sys:menu:delete")
    @ApiOperation("删除菜单")
    public Result remove(@PathVariable("menuId") long menuId) {
        Result<Object> result = new Result<>();
        if (menuId <= 31) {
            return result.error("系统菜单，不能删除");
        }

        //判断是否有子菜单或按钮
        List<SysMenuEntity> menuList = sysMenuService.queryListParentId(menuId);
        if (menuList.size() > 0) {
            return result.error("请先删除子菜单或按钮");
        }

        sysMenuService.delete(menuId);

        return result.ok();
    }

    /**
     * 验证参数是否正确
     */
    private void verifyForm(SysMenuEntity menu) {
        if (StringUtils.isBlank(menu.getName())) {
            throw new SevenmeException("菜单名称不能为空");
        }

        if (menu.getParentId() == null) {
            throw new SevenmeException("上级菜单不能为空");
        }

        //菜单
        if (menu.getType() == Constant.MenuType.MENU.getValue()) {
            if (StringUtils.isBlank(menu.getUrl())) {
                throw new SevenmeException("菜单URL不能为空");
            }
        }

        //上级菜单类型
        int parentType = Constant.MenuType.CATALOG.getValue();
        if (menu.getParentId() != 0) {
            SysMenuEntity parentMenu = sysMenuService.getById(menu.getParentId());
            parentType = parentMenu.getType();
        }

        //目录、菜单
        if (menu.getType() == Constant.MenuType.CATALOG.getValue() ||
                menu.getType() == Constant.MenuType.MENU.getValue()) {
            if (parentType != Constant.MenuType.CATALOG.getValue()) {
                throw new SevenmeException("上级菜单只能为目录类型");
            }
            return;
        }

        //按钮
        if (menu.getType() == Constant.MenuType.BUTTON.getValue()) {
            if (parentType != Constant.MenuType.MENU.getValue()) {
                throw new SevenmeException("上级菜单只能为菜单类型");
            }
            return;
        }
    }
}
