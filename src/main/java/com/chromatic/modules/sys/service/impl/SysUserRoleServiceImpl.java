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
import com.chromatic.common.utils.MapUtils;
import com.chromatic.modules.sys.dao.SysRoleDao;
import com.chromatic.modules.sys.dao.SysUserRoleDao;
import com.chromatic.modules.sys.entity.SysRoleEntity;
import com.chromatic.modules.sys.entity.SysUserRoleEntity;
import com.chromatic.modules.sys.service.SysUserRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * 用户与角色对应关系
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service("sysUserRoleService")
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleDao, SysUserRoleEntity> implements SysUserRoleService {

    @Resource
    SysUserRoleDao sysUserRoleDao;

    @Resource
    SysRoleDao sysRoleDao;

    @Override
    public void saveOrUpdate(Long userId, List<Long> roleIdList) {
        //先删除用户与角色关系
        this.removeByMap(new MapUtils().put("user_id", userId));

        if (roleIdList == null || roleIdList.size() == 0) {
            return;
        }

        //保存用户与角色关系
        for (Long roleId : roleIdList) {
            SysUserRoleEntity sysUserRoleEntity = new SysUserRoleEntity();
            sysUserRoleEntity.setUserId(userId);
            sysUserRoleEntity.setRoleId(roleId);

            this.save(sysUserRoleEntity);
        }
    }

    @Override
    public List<Long> queryRoleIdList(Long userId) {
        return baseMapper.queryRoleIdList(userId);
    }

    @Override
    public int deleteBatch(Long[] roleIds) {
        return baseMapper.deleteBatch(roleIds);
    }

    public boolean isSuper(Long userId) {
        List<Long> roleIdList = sysUserRoleDao.queryRoleIdList(userId);
        QueryWrapper<SysRoleEntity> roleEntityQueryWrapper = new QueryWrapper<>();
        roleEntityQueryWrapper.lambda().select(SysRoleEntity::getIsSuper).in(SysRoleEntity::getId, roleIdList);
        List<SysRoleEntity> roleEntityList = sysRoleDao.selectList(roleEntityQueryWrapper);

        for (SysRoleEntity sysRoleEntity : roleEntityList) {
            if (sysRoleEntity.getIsSuper()) {
                return true;
            }
        }
        return false;
    }
}
