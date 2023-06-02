/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.modules.sys.controller;

import com.chromatic.common.utils.JwtUtils;
import com.chromatic.common.vo.Result;
import com.chromatic.modules.sys.entity.SysUserEntity;
import com.chromatic.modules.sys.form.SysLoginForm;
import com.chromatic.modules.sys.service.SysCaptchaService;
import com.chromatic.modules.sys.service.SysUserService;
import com.chromatic.modules.sys.service.SysUserTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * 登录相关
 *
 * @author chromatic
 */
@RestController
@Api(tags = "系统登录相关")
public class SysLoginController extends AbstractController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserTokenService sysUserTokenService;
    @Autowired
    private SysCaptchaService sysCaptchaService;

    @Autowired
    private JwtUtils jwtUtils;


    @GetMapping("captcha.jpg")
    @ApiOperation(value = "获取验证码")
    public Result<Object> captcha(HttpServletResponse response) {
        // response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        //获取图片验证码
        Map<String, String> verifiedCode = sysCaptchaService.getVerifiedCode();
        Result<Object> result = new Result<>();
        result.setCode(200);
        result.setData(verifiedCode);
        return result;
    }

    /**
     * 登录
     */
    @PostMapping("/sys/login")
    @ApiOperation(value = "登录")
    public Result<Object> login(@RequestBody SysLoginForm form) throws IOException {
        Result<Object> result = new Result<>();

        String username = form.getName();
        String password = form.getPassword();

        boolean captcha = sysCaptchaService.validate(form.getUuid(), form.getCaptcha());
        if (!captcha) {
            return result.error("验证码不正确");
        }

        //用户信息
        SysUserEntity user = sysUserService.queryByUserName(form.getName());


        //账号不存在、密码错误
        if (user == null || !user.getPassword().equals(new Sha256Hash(form.getPassword(), user.getSalt()).toHex())) {
            return result.error("账号或密码不正确");
        }

        //账号锁定
        if (user.getStatus() == 0) {
            return result.error("账号已被锁定,请联系管理员");
        }

        LocalDateTime localDateTime = user.getExpirationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        long expiredMillis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        //获取当前时间的Instant对象并转换为毫秒值
        long nowMillis = Instant.now().toEpochMilli();
        //账号过期
        if (user == null || nowMillis >= expiredMillis) {
            return result.error("账号已过期");
        }

        // 生成 token
        result = sysUserTokenService.createToken(user.getId());

        return result;
    }

    /**
     * 退出
     */
    @PostMapping("/sys/logout")
    @ApiOperation(value = "退出")
    public Result logout() {
        Result<Object> result = new Result<>();
        // sysUserTokenService.logout(getUserId());
        return result.ok();
    }

}
