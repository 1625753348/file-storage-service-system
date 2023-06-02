/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chromatic.common.utils.JwtUtils;
import com.chromatic.common.vo.Result;
import com.chromatic.modules.sys.dao.SysUserTokenDao;
import com.chromatic.modules.sys.entity.SysUserTokenEntity;
import com.chromatic.modules.sys.service.SysUserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service("sysUserTokenService")
public class SysUserTokenServiceImpl extends ServiceImpl<SysUserTokenDao, SysUserTokenEntity> implements SysUserTokenService {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Result<Object> createToken(long userId) {
        Result<Object> result = new Result<>();
        //生成一个token
        String token = jwtUtils.generateToken(userId);

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", token);
        hashMap.put("expire", jwtUtils.getExpire());
        result.setData(hashMap);
        return result;
    }

    @Override
    public void logout(long userId) {
        // jwtUtils
        // SysUserTokenEntity tokenEntity = new SysUserTokenEntity();
        // tokenEntity.setUserId(userId);
        // tokenEntity.setToken("");
        // this.updateById(tokenEntity);
    }
}
