/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.modules.sys.controller;


import com.chromatic.common.annotation.sys.SysLog;
import com.chromatic.common.utils.PageUtils;
import com.chromatic.common.validator.ValidatorUtils;
import com.chromatic.common.vo.Result;
import com.chromatic.modules.sys.entity.SysConfigEntity;
import com.chromatic.modules.sys.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统配置信息
 *
 * @author chromatic
 */
@RestController
@RequestMapping("/sys/config")
@Api(tags = "系统配置信息")
public class SysConfigController extends AbstractController {
    @Autowired
    private SysConfigService sysConfigService;

    @GetMapping("/page")
    // @RequiresPermissions(value = "sys:config:list")
    @ApiOperation("所有配置列表")
    public Result page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageUtils page = sysConfigService.queryPage(params);

        Result<Object> result = new Result<>();
        result.setData(page);
        return result.ok();
    }

    /**
     * 所有配置列表
     */
    @GetMapping("/list")
    // @RequiresPermissions(value = "sys:config:list")
    @ApiOperation("所有配置列表")
    public Result list(@ApiIgnore @RequestParam Map<String, Object> params) {
        Result<Object> result = new Result<>();

        List<SysConfigEntity> list = sysConfigService.list(params);

        result.setData(list);
        return result;
    }

    /**
     * 配置信息
     */
    @GetMapping("/info/{id}")
    // @RequiresPermissions(value = "sys:config:info")
    @ApiOperation("配置信息")
    public Result info(@PathVariable("id") Long id) {
        SysConfigEntity config = sysConfigService.getById(id);

        Result<Object> result = new Result<>();
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("config", config);
        result.setData(hashMap);
        return result.ok();
    }

    /**
     * 保存配置
     */
    @SysLog("保存配置")
    @PostMapping("/add")
    // @RequiresPermissions(value = "sys:config:save")
    @ApiOperation("保存配置")
    public Result add(@RequestBody SysConfigEntity config) {
        ValidatorUtils.validateEntity(config);

        sysConfigService.saveConfig(config);

        Result<Object> result = new Result<>();
        return result.ok();
    }

    /**
     * 修改配置
     */
    @SysLog("修改配置")
    @PostMapping("/update")
    // @RequiresPermissions(value = "sys:config:update")
    @ApiOperation("修改配置")
    public Result update(@RequestBody SysConfigEntity config) {
        ValidatorUtils.validateEntity(config);

        sysConfigService.update(config);

        Result<Object> result = new Result<>();
        return result.ok();
    }

    /**
     * 删除配置
     */
    @SysLog("删除配置")
    @PostMapping("/remove")
    // @RequiresPermissions(value = "sys:config:delete")
    @ApiOperation("删除配置")
    public Result remove(@RequestBody Long[] ids) {
        sysConfigService.deleteBatch(ids);

        Result<Object> result = new Result<>();
        return result.ok();
    }

}
