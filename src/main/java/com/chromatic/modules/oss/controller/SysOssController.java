

package com.chromatic.modules.oss.controller;

import com.chromatic.modules.sys.service.SysConfigService;
import com.google.gson.Gson;
import com.chromatic.common.exception.SevenmeException;
import com.chromatic.common.utils.ConfigConstant;
import com.chromatic.common.utils.Constant;
import com.chromatic.common.utils.PageUtils;
import com.chromatic.common.validator.ValidatorUtils;
import com.chromatic.common.validator.group.AliyunGroup;
import com.chromatic.common.validator.group.QcloudGroup;
import com.chromatic.common.validator.group.QiniuGroup;
import com.chromatic.common.vo.Result;
import com.chromatic.modules.oss.cloud.CloudStorageConfig;
import com.chromatic.modules.oss.cloud.OSSFactory;
import com.chromatic.modules.oss.entity.SysOssEntity;
import com.chromatic.modules.oss.service.SysOssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("sys/oss")
public class SysOssController {
    @Autowired
    private SysOssService sysOssService;
    @Autowired
    private SysConfigService sysConfigService;

    private final static String KEY = ConfigConstant.CLOUD_STORAGE_CONFIG_KEY;

    /**
     * 列表
     */
    @GetMapping("/list")
    // @RequiresPermissions(value = "sys:oss:all")
    public Result list(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageUtils page = sysOssService.queryPage(params);

        Result<Object> result = new Result<>();
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("page", page);
        result.setData(hashMap);
        return result.ok();
    }


    /**
     * 云存储配置信息
     */
    @GetMapping("/config")
    // @RequiresPermissions(value = "sys:oss:all")
    public Result config() {
        CloudStorageConfig config = sysConfigService.getConfigObject(KEY, CloudStorageConfig.class);

        Result<Object> result = new Result<>();
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("config", config);
        result.setData(hashMap);
        return result.ok();
    }


    /**
     * 保存云存储配置信息
     */
    @PostMapping("/saveConfig")
    // @RequiresPermissions(value = "sys:oss:all")
    public Result saveConfig(@RequestBody CloudStorageConfig config) {
        //校验类型
        ValidatorUtils.validateEntity(config);

        if (config.getType() == Constant.CloudService.QINIU.getValue()) {
            //校验七牛数据
            ValidatorUtils.validateEntity(config, QiniuGroup.class);
        } else if (config.getType() == Constant.CloudService.ALIYUN.getValue()) {
            //校验阿里云数据
            ValidatorUtils.validateEntity(config, AliyunGroup.class);
        } else if (config.getType() == Constant.CloudService.QCLOUD.getValue()) {
            //校验腾讯云数据
            ValidatorUtils.validateEntity(config, QcloudGroup.class);
        }

        sysConfigService.updateValueByKey(KEY, new Gson().toJson(config));

        Result<Object> result = new Result<>();
        return result.ok();
    }


    /**
     * 上传文件
     */
    @PostMapping("/upload")
    // @RequiresPermissions(value = "sys:oss:all")
    public Result upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new SevenmeException("上传文件不能为空");
        }

        //上传文件
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String url = OSSFactory.build().uploadSuffix(file.getBytes(), suffix);

        //保存文件信息
        SysOssEntity ossEntity = new SysOssEntity();
        ossEntity.setUrl(url);
        ossEntity.setCreateDate(new Date());
        sysOssService.save(ossEntity);

        Result<Object> result = new Result<>();
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("url", url);
        result.setData(hashMap);
        return result.ok();
    }


    /**
     * 删除
     */
    @PostMapping("/remove")
    // @RequiresPermissions(value = "sys:oss:all")
    public Result remove(@RequestBody Long[] ids) {
        sysOssService.removeByIds(Arrays.asList(ids));

        Result<Object> result = new Result<>();
        return result.ok();
    }

}
