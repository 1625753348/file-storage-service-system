

package com.chromatic.modules.oss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chromatic.modules.oss.entity.SysOssEntity;
import com.chromatic.common.utils.PageUtils;

import java.util.Map;

/**
 * 文件上传
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysOssService extends IService<SysOssEntity> {

    PageUtils queryPage(Map<String, Object> params);
}
