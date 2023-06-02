/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.modules.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chromatic.modules.sys.service.SysUserRoleService;
import com.chromatic.common.exception.SevenmeException;
import com.chromatic.common.utils.CommonUtils;
import com.chromatic.common.utils.PageUtils;
import com.chromatic.common.utils.Query;

import com.chromatic.modules.sys.dao.SysRoleDao;
import com.chromatic.modules.sys.dao.SysUserRoleDao;
import com.chromatic.modules.sys.entity.SysRoleEntity;
import com.chromatic.modules.sys.entity.SysUserEntity;
import com.chromatic.modules.sys.entity.SysUserRoleEntity;
import com.chromatic.modules.sys.service.SysRoleMenuService;
import com.chromatic.modules.sys.service.SysRoleService;
import com.chromatic.modules.sys.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service("sysRoleService")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleDao, SysRoleEntity> implements SysRoleService {
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleDao sysRoleDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String roleName = (String) params.get("roleName");
        Long createUserId = (Long) params.get("createUserId");

        IPage<SysRoleEntity> page = this.page(
                new Query<SysRoleEntity>().getPage(params),
                new QueryWrapper<SysRoleEntity>()
                        .like(StringUtils.isNotBlank(roleName), "role_name", roleName)
                        .eq(createUserId != null, "create_user_id", createUserId)
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(SysRoleEntity role) {
        role.setCreateTime(new Date());
        role.setIsSuper(false);
        this.save(role);

        //检查权限是否越权
        checkPrems(role);

        //保存角色与菜单关系
        sysRoleMenuService.saveOrUpdate(role.getId(), role.getMenuIdList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysRoleEntity role) {

        SysRoleEntity Entity = sysRoleDao.selectById(role.getId());
        role.setCreateTime(Entity.getCreateTime());
        role.setIsSuper(Entity.getIsSuper());
        role.setCreateUserId(Entity.getCreateUserId());
        //检查权限是否越权
        checkPrems(role);
        this.updateById(role);

        //更新角色与菜单关系
        sysRoleMenuService.saveOrUpdate(role.getId(), role.getMenuIdList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] roleIds) {
        //删除角色
        this.removeByIds(Arrays.asList(roleIds));

        //删除角色与菜单关联
        sysRoleMenuService.deleteBatch(roleIds);

        //删除角色与用户关联
        sysUserRoleService.deleteBatch(roleIds);
    }


    @Override
    public List<Long> queryRoleIdList(Long createUserId) {
        return baseMapper.queryRoleIdList(createUserId);
    }

    /**
     * 检查权限是否越权
     */
    @Autowired
    SysUserRoleDao sysUserRoleDao;
    private void  checkPrems(SysRoleEntity role) {
        //如果不是超级管理员，则需要判断角色的权限是否超过自己的权限
        SysUserEntity user = ((SysUserEntity) SecurityUtils.getSubject().getPrincipal());

        List<SysUserRoleEntity> sysUserRoleEntities = sysUserRoleDao.selectList(new QueryWrapper<SysUserRoleEntity>().lambda().eq(SysUserRoleEntity::getUserId, user.getId()));

        if (sysUserRoleEntities != null) {
            Set<Long> roleIds = sysUserRoleEntities.stream().map(SysUserRoleEntity::getRoleId).collect(Collectors.toSet());
            List<SysRoleEntity> sysRoleEntityList = sysRoleDao.selectList(new QueryWrapper<SysRoleEntity>().lambda().in(SysRoleEntity::getId, roleIds));
            Set<Boolean> is = sysRoleEntityList.stream().map(SysRoleEntity::getIsSuper).collect(Collectors.toSet());

            if (is.contains(true)){
                return;
            }
        }



        //查询用户所拥有的菜单列表
        List<Long> menuIdList = sysUserService.queryAllMenuId(role.getCreateUserId());

        //判断是否越权
        if (!menuIdList.containsAll(role.getMenuIdList())) {
            throw new SevenmeException("新增角色的权限，已超出你的权限范围");
        }
    }

    @Override
    public List<SysRoleEntity> list(Map<String, Object> params) {
        Map<String, Object> filterMap = new HashMap<>(params);
        List<SysRoleEntity> entityList = sysRoleDao.selectList(getWrapper(params));

        entityList.stream().forEach(e->{
            List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(e.getId());
            e.setMenuIdList(menuIdList);
        });


        return entityList;

    }

    public QueryWrapper<SysRoleEntity> getWrapper(Map<String, Object> params) throws NumberFormatException, ClassCastException {
        QueryWrapper<SysRoleEntity> queryWrapper = new QueryWrapper<>();
       // queryWrapper.lambda().and(QueryWrapper -> QueryWrapper.ne(SysRoleEntity::getIsSuper,1).or().isNull(SysRoleEntity::getIsSuper));
        queryWrapper.orderByDesc("update_time");
        if (ObjectUtils.isEmpty(params)) {
            return queryWrapper;
        }

        Map<String, Object> hashMap = CommonUtils.getParamMap(params);

        queryWrapper.lambda().like(hashMap.containsKey("role_name") ,SysRoleEntity::getRoleName,hashMap.get("role_name"));
        hashMap.remove("role_name");

        queryWrapper.allEq(hashMap);
        return queryWrapper;

    }
}
