/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.sevenme.service;

import com.chromatic.datasource.annotation.DataSource;
import com.chromatic.modules.sys.dao.SysUserDao;
import com.chromatic.modules.sys.entity.SysUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 测试多数据源
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
//@DataSource("slave1")
public class DynamicDataSourceTestService {
    @Autowired
    private SysUserDao sysUserDao;

    @Transactional
    public void updateUser(Long id) {
        SysUserEntity user = new SysUserEntity();
        user.setId(id);
        sysUserDao.updateById(user);
    }

    @Transactional
    @DataSource("slave1")
    public void updateUserBySlave1(Long id) {
        SysUserEntity user = new SysUserEntity();
        user.setId(id);
        sysUserDao.updateById(user);
    }

    @DataSource("slave2")
    @Transactional
    public void updateUserBySlave2(Long id) {
        SysUserEntity user = new SysUserEntity();
        user.setId(id);
        sysUserDao.updateById(user);

        //测试事物
        int i = 1 / 0;
    }
}