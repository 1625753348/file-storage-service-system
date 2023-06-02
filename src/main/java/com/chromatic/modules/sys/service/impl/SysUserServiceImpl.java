/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.modules.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chromatic.common.exception.SevenmeException;
import com.chromatic.common.utils.CommonUtils;
import com.chromatic.common.utils.PageUtils;
import com.chromatic.common.utils.Query;

import com.chromatic.modules.sys.dao.SysUserDao;
import com.chromatic.modules.sys.dto.SysUserStaffDTO;

import com.chromatic.modules.sys.entity.SysRoleEntity;
import com.chromatic.modules.sys.entity.SysUserEntity;
import com.chromatic.modules.sys.entity.SysUserRoleEntity;
import com.chromatic.modules.sys.service.SysRoleService;
import com.chromatic.modules.sys.service.SysUserRoleService;
import com.chromatic.modules.sys.service.SysUserService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 系统用户
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    SysUserDao sysUserDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String username = (String) params.get("name");
        Long createUserId = (Long) params.get("createUserId");

        IPage<SysUserEntity> page = this.page(
                new Query<SysUserEntity>().getPage(params),
                new QueryWrapper<SysUserEntity>()
                        .like(StringUtils.isNotBlank(username), "username", username)
                        .eq(createUserId != null, "create_user_id", createUserId)
        );

        return new PageUtils(page);
    }

    @Override
    public List<SysUserEntity> getAllUsers(Map<String, Object> params) {
        List<SysUserEntity> list = this.list(getWrapper(params));
        List<SysUserRoleEntity> roleEntityList = sysUserRoleService.list(new QueryWrapper<>());
        List<SysRoleEntity> sysRoleEntityList = sysRoleService.list(new QueryWrapper<>());
        Map<Long, List<SysRoleEntity>> roleIDGroup = sysRoleEntityList.stream().collect(Collectors.groupingBy(SysRoleEntity::getId));
        Map<Long, List<SysUserRoleEntity>> userIDGroup = roleEntityList.stream().collect(Collectors.groupingBy(SysUserRoleEntity::getUserId));
        list.forEach(entity -> {
            List<SysUserRoleEntity> roleEntitys = userIDGroup.get(entity.getId());
            roleEntitys.forEach(role -> {
                entity.setRoleList(roleIDGroup.get(role.getRoleId()));
            });
        });
        return list;
    }

    @Override
    public SysUserStaffDTO getInfoByCurrentId() {
        return null;
    }


    private void setNull(SysUserEntity sysUser) {
        sysUser.setCreateTime(null);
        sysUser.setUpdateTime(null);
        sysUser.setCreateUser(null);
        sysUser.setUpdateUser(null);
    }

    @Override
    public int resetPassword(String id) {
        final SysUserEntity sysUser = sysUserDao.selectById(id);
        if (sysUser != null) {
            setNull(sysUser);
            sysUser.setPassword(new Sha256Hash("DKFc0YasgSuPnPIP", sysUser.getSalt()).toHex());
            sysUser.setIsUserPassword(0);

            return sysUserDao.updateById(sysUser);
        }
        return 0;
    }

    @Override
    public List<String> queryAllPerms(Long userId) {
        return baseMapper.queryAllPerms(userId);
    }

    @Override
    public List<Long> queryAllMenuId(Long userId) {
        return baseMapper.queryAllMenuId(userId);
    }

    @Override
    public SysUserEntity queryByUserName(String username) {
        return baseMapper.queryByUserName(username);
    }

    @Override
    @Transactional
    public void saveUser(SysUserEntity user) {
        //检验是admin root 提示用户名重复
        if (user.getName().equals("admin") || user.getName().equals("root")){
            throw new SevenmeException("用户名重复");
        }
        String username = ((SysUserEntity) SecurityUtils.getSubject().getPrincipal()).getName();
        user.setCreateTime(new Date());
        user.setCreateUser(username);
        user.setUpdateUser(username);
        user.setUpdateTime(new Date());
        user.setUpdateTime(new Date());

        //sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        //user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());

        user.setPassword(new Sha256Hash("DKFc0YasgSuPnPIP", salt).toHex());
        user.setIsUserPassword(0);
        user.setSalt(salt);
        this.save(user);

        //检查角色是否越权
        checkRole(user);

        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(user.getId(), user.getRoleIdList());
    }

    @Override
    @Transactional
    public void update(SysUserEntity user) {
        if (user.getName().equals("admin") || user.getName().equals("root")){
            throw new SevenmeException("用户名重复");
        }
        SysUserEntity sysUserEntity = sysUserDao.selectById(user.getId());

        SysUserEntity account = ((SysUserEntity) SecurityUtils.getSubject().getPrincipal());
//        if (StringUtils.isBlank(user.getPassword())) {
//            user.setPassword(null);
//        } else {
//            user.setPassword(new Sha256Hash(user.getPassword(), user.getSalt()).toHex());
//        }
        user.setPassword(sysUserEntity.getPassword());
        user.setSalt(sysUserEntity.getSalt());

        user.setIsUserPassword(sysUserEntity.getIsUserPassword());
        user.setUpdateUser(account.getName());
        user.setUpdateTime(new Date());
        this.updateById(user);

        //检查角色是否越权
        checkRole(user);

        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(user.getId(), user.getRoleIdList());
    }

    @Override
    public void deleteBatch(Long[] userId) {
        this.removeByIds(Arrays.asList(userId));
    }

    @Override
    public boolean updatePassword(Long userId, String password, String newPassword) {
        SysUserEntity userEntity = new SysUserEntity();
        userEntity.setPassword(newPassword);
        return this.update(null,
                new UpdateWrapper<SysUserEntity>().lambda()
                        .set(SysUserEntity::getPassword,newPassword)
                        .set(SysUserEntity::getIsUserPassword,1)
                        .eq(SysUserEntity::getId, userId)
                        .eq(SysUserEntity::getPassword, password));

    }
    @Override
    public boolean updateSysPassword(Long userId, String newPassword) {
        SysUserEntity userEntity = new SysUserEntity();
        userEntity.setPassword(newPassword);
        return this.update(null,
                new UpdateWrapper<SysUserEntity>().lambda().set(SysUserEntity::getPassword,newPassword).eq(SysUserEntity::getId, userId));
    }
    @Override
    public boolean updateStatus(Long userId, String status) {
        SysUserEntity userEntity = new SysUserEntity();
        userEntity.setStatus(Integer.parseInt(status));
        return this.update(userEntity,
                new QueryWrapper<SysUserEntity>().eq("id", userId).eq("status", status));
    }

    /**
     * 检查角色是否越权
     */
    private void checkRole(SysUserEntity user) {
        if (user.getRoleIdList() == null || user.getRoleIdList().size() == 0) {
            return;
        }
        //如果不是超级管理员，则需要判断用户的角色是否自己创建
        if (sysUserRoleService.isSuper(user.getCreateUserId())){
            return;
        }

        //查询用户创建的角色列表
        List<Long> roleIdList = sysRoleService.queryRoleIdList(user.getCreateUserId());

        //判断是否越权
        if (!roleIdList.containsAll(user.getRoleIdList())) {
            throw new SevenmeException("新增用户所选角色，不是本人创建");
        }
    }

    public QueryWrapper<SysUserEntity> getWrapper(Map<String, Object> params) throws NumberFormatException, ClassCastException {
        QueryWrapper<SysUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("update_time");
        if (ObjectUtils.isEmpty(params)) {
            return queryWrapper;
        }

        queryWrapper
                .lambda()
                .like(params.containsKey("name"),SysUserEntity::getName,params.get("name"));

        params.remove("name");

        Map<String, Object> hashMap = CommonUtils.getParamMap(params);
        queryWrapper.allEq(hashMap);
        return queryWrapper;
    }
}