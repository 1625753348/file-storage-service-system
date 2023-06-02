/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chromatic.modules.sys.dao.SysMenuDao;
import com.chromatic.modules.sys.dao.SysRoleDao;
import com.chromatic.modules.sys.dao.SysRoleMenuDao;
import com.chromatic.modules.sys.dao.SysUserDao;
import com.chromatic.modules.sys.entity.SysMenuEntity;
import com.chromatic.modules.sys.entity.SysRoleEntity;
import com.chromatic.modules.sys.entity.SysRoleMenuEntity;
import com.chromatic.modules.sys.entity.SysUserEntity;

import com.chromatic.modules.sys.service.SysUserRoleService;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller公共组件
 *
 * @author chromatic
 */
public abstract class AbstractController {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    SysUserRoleService sysUserRoleService;
    @Autowired
    SysUserDao sysUserDao;
    @Autowired
    SysMenuDao sysMenuDao;
    @Autowired
    SysRoleDao sysRoleDao;
    @Autowired
    SysRoleMenuDao sysRoleMenuDao;

    protected SysUserEntity getUser() {
        SysUserEntity data =  (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
        List<Long> idList = sysUserRoleService.queryRoleIdList(data.getId());
        data.setRoleIdList(idList);
        List<SysRoleEntity> sysRoleEntityList = sysRoleDao.selectList(new QueryWrapper<SysRoleEntity>().lambda().in(SysRoleEntity::getId,idList));
        data.setRoleList(sysRoleEntityList);
        if (idList != null) {
            List<SysRoleMenuEntity> sysRoleMenuEntityList = sysRoleMenuDao.selectList(new QueryWrapper<SysRoleMenuEntity>().lambda().in(SysRoleMenuEntity::getRoleId,idList));

            if (sysRoleMenuEntityList.size()!=0){

                Set<Long> menuIds = sysRoleMenuEntityList.stream().map(SysRoleMenuEntity::getMenuId).collect(Collectors.toSet());
                if (menuIds != null) {
                    List<SysMenuEntity> sysMenuEntityList = sysMenuDao.selectList(new QueryWrapper<SysMenuEntity>().lambda().in(SysMenuEntity::getId,menuIds));
                    data.setMenuList(sysMenuEntityList);

                }
            }
        }
        return data;
    }

    protected SysUserEntity getSysUser(Long sysUserId) {
        SysUserEntity data =  sysUserDao.selectById(sysUserId);
        List<Long> idList = sysUserRoleService.queryRoleIdList(sysUserId);
        data.setRoleIdList(idList);
        List<SysRoleEntity> sysRoleEntityList = sysRoleDao.selectList(new QueryWrapper<SysRoleEntity>().lambda().in(SysRoleEntity::getId,idList));
        data.setRoleList(sysRoleEntityList);
        if (idList != null) {
            List<SysRoleMenuEntity> sysRoleMenuEntityList = sysRoleMenuDao.selectList(new QueryWrapper<SysRoleMenuEntity>().lambda().in(SysRoleMenuEntity::getRoleId,idList));

            if (sysRoleMenuEntityList.size()!=0){

                Set<Long> menuIds = sysRoleMenuEntityList.stream().map(SysRoleMenuEntity::getMenuId).collect(Collectors.toSet());
                if (menuIds != null) {
                    List<SysMenuEntity> sysMenuEntityList = sysMenuDao.selectList(new QueryWrapper<SysMenuEntity>().lambda().in(SysMenuEntity::getId,menuIds));
                    data.setMenuList(sysMenuEntityList);
                }
            }
        }
        return data;
    }
    protected Long getUserId() {
        SysUserEntity f = getUser();
        Long d  = f.getId();
        return getUser().getId();
    }
}
