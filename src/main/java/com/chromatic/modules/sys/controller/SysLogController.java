/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.modules.sys.controller;

import com.chromatic.common.utils.PageUtils;
import com.chromatic.common.vo.Result;
import com.chromatic.modules.sys.service.SysLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;


/**
 * 系统日志
 *
 * @author Mark sunlightcs@gmail.com
 */
@Controller
@RequestMapping("/sys/log")
@Api(tags = "系统日志")
public class SysLogController {
    @Autowired
    private SysLogService sysLogService;

    /**
     * 列表
     */
    @ResponseBody
    @GetMapping("/page")
    // @RequiresPermissions(value = "sys:log:list")
    @ApiOperation("列表")
    public Result page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageUtils page = sysLogService.queryPage(params);

        Result<Object> result = new Result<>();
        result.setData(page);
        return result.ok();
    }

}
