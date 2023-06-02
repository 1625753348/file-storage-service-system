/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.modules.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chromatic.common.utils.Constant;
import com.chromatic.common.utils.MapUtils;
import com.chromatic.modules.sys.dao.SysMenuDao;
import com.chromatic.modules.sys.entity.SysMenuEntity;
import com.chromatic.modules.sys.service.SysMenuService;
import com.chromatic.modules.sys.service.SysRoleMenuService;
import com.chromatic.modules.sys.service.SysUserRoleService;
import com.chromatic.modules.sys.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


@Service("sysMenuService")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuDao, SysMenuEntity> implements SysMenuService {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Resource
    private SysUserRoleService sysUserRoleService;

    @Override
    public List<SysMenuEntity> queryListParentId(Long parentId, List<Long> menuIdList) {
        List<SysMenuEntity> menuList = queryListParentId(parentId);
        if (menuIdList == null) {
            return menuList;
        }

        List<SysMenuEntity> userMenuList = new ArrayList<>();
        for (SysMenuEntity menu : menuList) {
            if (menuIdList.contains(menu.getId())) {
                userMenuList.add(menu);
            }
        }
        return userMenuList;
    }

    @Override
    public List<SysMenuEntity> queryListParentId(Long parentId) {
        return baseMapper.queryListParentId(parentId);
    }

    @Override
    public List<SysMenuEntity> queryNotButtonList() {
        return baseMapper.queryNotButtonList();
    }

    @Override
    public List<SysMenuEntity> getUserMenuList(Long userId) {
        //系统管理员，拥有最高权限
        if (sysUserRoleService.isSuper(userId)){
            return getMenuList(null);
        }

        //用户菜单列表
        List<Long> menuIdList = sysUserService.queryAllMenuId(userId);

        return getMenuList(menuIdList);
    }

    /**
     * 获取拥有的菜单列表
     *
     * @param menuIdList
     * @return
     */
    private List<SysMenuEntity> getMenuList(List<Long> menuIdList) {
        // 查询拥有的所有菜单
        List<SysMenuEntity> menus = this.baseMapper.selectList(new QueryWrapper<SysMenuEntity>()
                .in(Objects.nonNull(menuIdList), "id", menuIdList).in("type", 0, 1));
        // 将id和菜单绑定
        HashMap<Long, SysMenuEntity> menuMap = new HashMap<>(12);
        for (SysMenuEntity s : menus) {
            menuMap.put(s.getId(), s);
        }
        // 使用迭代器,组装菜单的层级关系
        Iterator<SysMenuEntity> iterator = menus.iterator();
        while (iterator.hasNext()) {
            SysMenuEntity menu = iterator.next();
            SysMenuEntity parent = menuMap.get(menu.getParentId());
            if (Objects.nonNull(parent)) {
                parent.getList().add(menu);
                // 将这个菜单从当前节点移除
                iterator.remove();
            }
        }

        return menus;
    }

    /************************************************************************
     * @author: wg
     * @description: 简单点
     * @params:
     * @return:
     * @createTime: 11:49  2022/3/22
     * @updateTime: 11:49  2022/3/22
     ************************************************************************/
    public List<SysMenuEntity> getMenus(Long userId) {
        //系统管理员，拥有最高权限
        if (sysUserRoleService.isSuper(userId)){
            return super.list();
        }

        //用户菜单列表
        List<Long> menuIdList = sysUserService.queryAllMenuId(userId);
        // 查询拥有的所有菜单
        List<SysMenuEntity> menus = this.baseMapper.selectList(new QueryWrapper<SysMenuEntity>()
                .in(Objects.nonNull(menuIdList), "id", menuIdList).in("type", 0, 1));

        return menus;
    }

    public List<SysMenuEntity> getMenusByRoleId(Long roleId) {
        //系统管理员，拥有最高权限
        if (roleId == 0L) {
            return super.list();
        }

        //用户菜单列表
        // List<Long> menuIdList = sysUserService.queryAllMenuId(roleId);
        List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(roleId);
        // 查询拥有的所有菜单
        List<SysMenuEntity> menus = this.baseMapper.selectList(new QueryWrapper<SysMenuEntity>()
                .in(Objects.nonNull(menuIdList), "id", menuIdList).in("type", 0, 1));

        return menus;
    }

    @Override
    public void delete(Long menuId) {
        //删除菜单
        this.removeById(menuId);
        //删除菜单与角色关联
        sysRoleMenuService.removeByMap(new MapUtils().put("id", menuId));
    }

    /**
     * 获取所有菜单列表
     */
    private List<SysMenuEntity> getAllMenuList(List<Long> menuIdList) {
        //查询根菜单列表
        List<SysMenuEntity> menuList = queryListParentId(0L, menuIdList);
        //递归获取子菜单
        getMenuTreeList(menuList, menuIdList);

        return menuList;
    }

    /**
     * 递归
     */
    private List<SysMenuEntity> getMenuTreeList(List<SysMenuEntity> menuList, List<Long> menuIdList) {
        List<SysMenuEntity> subMenuList = new ArrayList<SysMenuEntity>();

        for (SysMenuEntity entity : menuList) {
            //目录
            if (entity.getType() == Constant.MenuType.CATALOG.getValue()) {
                entity.setList(getMenuTreeList(queryListParentId(entity.getId(), menuIdList), menuIdList));
            }
            subMenuList.add(entity);
        }

        return subMenuList;
    }
}
