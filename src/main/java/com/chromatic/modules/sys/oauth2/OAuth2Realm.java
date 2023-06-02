/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.modules.sys.oauth2;

import com.chromatic.modules.sys.service.ShiroService;
import com.chromatic.common.exception.SevenmeException;
import com.chromatic.common.utils.JwtUtils;
import com.chromatic.modules.sys.dao.SysUserTokenDao;
import com.chromatic.modules.sys.entity.SysUserEntity;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 认证
 *  realm最主要的职责就是“身份验证”（认证）和“授权”
 * @author Mark sunlightcs@gmail.com
 */
@Component
public class OAuth2Realm extends AuthorizingRealm {
    @Autowired
    private ShiroService shiroService;

    @Autowired
    private SysUserTokenDao sysUserTokenDao;

    @Resource
    private JwtUtils jwtUtils;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    /**
     * 授权(验证权限时调用)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SysUserEntity user = (SysUserEntity) principals.getPrimaryPrincipal();
        Long userId = user.getId();

        //用户权限列表
        Set<String> permsSet = shiroService.getUserPermissions(userId);

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);
        return info;
    }

    /**
     * 认证(登录时调用)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String accessToken = (String) token.getPrincipal();

        if (StringUtils.isNotBlank(accessToken)) {
            jwtUtils.isTokenExpired(accessToken);
        }

        // 验证 token
        Claims claims = null;
        try {
            claims = jwtUtils.analyseToken(accessToken);
        } catch (Exception e) {
            throw new IncorrectCredentialsException("token失效，请重新登录");
        }

        boolean tokenExpired = jwtUtils.isTokenExpired(claims);
        if (tokenExpired) {
            throw new SevenmeException("登录过期, 请重新登录");
        }

        //查询用户信息
        String subject = claims.getSubject();
        SysUserEntity user = shiroService.queryUser(Long.parseLong(subject));
        // SysUserEntity user = shiroService.queryUser(tokenEntity.getUserId());

        //账号锁定
        if (user.getStatus() == 0) {
            throw new LockedAccountException("账号已被锁定,请联系管理员");
        }

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, accessToken, getName());
        return info;
    }
}
