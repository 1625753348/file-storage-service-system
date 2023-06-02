/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chromatic.modules.sys.entity.SysCaptchaEntity;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * 验证码
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysCaptchaService extends IService<SysCaptchaEntity> {

    /**
     * 获取图片验证码
     */
    BufferedImage getCaptcha(String uuid);

    /**
     * 验证码效验
     *
     * @param uuid uuid
     * @param code 验证码
     * @return true：成功  false：失败
     */
    boolean validate(String uuid, String code);

    public Map<String, String> getVerifiedCode();
}
