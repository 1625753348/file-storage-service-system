

package com.chromatic.modules.app.controller;


import com.chromatic.modules.sys.entity.SysUserEntity;
import com.chromatic.common.annotation.app.LoginUser;
import com.chromatic.common.utils.JwtUtils;
import com.chromatic.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * APP测试接口
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/app")
@Api("APP测试接口")
public class AppTestController {

    @Resource
    JwtUtils jwtUtils;

    ////@Login
    @GetMapping("userInfo")
    @ApiOperation("获取登录用户的信息")
    public Result userInfo(@LoginUser SysUserEntity user) {
        Result<Object> result = new Result<>();
        result.setData(user);
        return result.ok();
    }

    ////@Login
    @GetMapping("userId")
    @ApiOperation("获取登录用户的ID")
    public Result userInfo(@RequestAttribute("userId") Long userId, HttpServletRequest request) {
        Object userId1 = request.getAttribute("userId");
        System.out.println("userId1  " + userId1 + "\n");
        System.out.println("userId  " + userId);
        Result<Object> result = new Result<>();
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("userId", userId);
        result.setData(hashMap);
        return result.ok();
    }

    ////@Login
    @GetMapping("/user_id")
    @ApiOperation("获取登录用户的ID, 在 jwtutils 里 userid 已经在了, 所以不用传userid")
    @ApiImplicitParams({
            // @ApiImplicitParam(name = "userId", value = "user id", paramType = "query", required = true, dataType = "int")
    })
    public Result userInfo1() {
        System.out.println("-------------");
        Result<Object> result = new Result<>();
        return result.ok();
    }

    @GetMapping("notToken")
    @ApiOperation("忽略Token验证测试")
    public Result notToken() {
        Result<Object> result = new Result<>();
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("msg", "无需token也能访问 ");
        return result.ok();
    }

}
