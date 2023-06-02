/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.chromatic.common.exception.SevenmeException;
import com.chromatic.common.utils.DateUtils;
import com.chromatic.modules.sys.dao.SysCaptchaDao;
import com.chromatic.modules.sys.entity.SysCaptchaEntity;
import com.chromatic.modules.sys.service.CacheService;
import com.chromatic.modules.sys.service.SysCaptchaService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * 验证码
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service("sysCaptchaService")
public class SysCaptchaServiceImpl extends ServiceImpl<SysCaptchaDao, SysCaptchaEntity> implements SysCaptchaService {
    @Autowired
    private Producer producer;
    @Autowired
    private CacheService cacheService;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public BufferedImage getCaptcha(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            throw new SevenmeException("uuid不能为空");
        }
        //生成文字验证码
        String code = producer.createText();

        SysCaptchaEntity captchaEntity = new SysCaptchaEntity();
        captchaEntity.setUuid(uuid);
        captchaEntity.setCode(code);
        //5分钟后过期
        captchaEntity.setExpireTime(DateUtils.addDateMinutes(new Date(), 5));
        this.save(captchaEntity);

        return producer.createImage(code);
    }

    public Map<String, String> getVerifiedCode(Integer width, Integer height, Integer lengthOfCode) {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();

        String text = defaultKaptcha.createText();
        BufferedImage image = defaultKaptcha.createImage(text);
        String codeKey = UUID.randomUUID().toString();
        cacheService.cacheVerifiedCode(codeKey, text);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "JPG", outputStream);
            byte[] bytes = outputStream.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String captchaBase64 = "data:image/jpeg;base64," + base64.replaceAll("\r\n", "");
            return new HashMap<String, String>() {{
                put("codeKey", codeKey);
                put("codeImg", captchaBase64);
            }};

        } catch (Exception e) {
            e.fillInStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, String> getVerifiedCode() {
        //生成文字验证码
        // DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        // String text = defaultKaptcha.createText();
        // BufferedImage image = defaultKaptcha.createImage(text);

        String text = producer.createText();
        BufferedImage image = producer.createImage(text);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        cacheService.cacheVerifiedCode(uuid, text);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "JPG", outputStream);
            byte[] bytes = outputStream.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String captchaBase64 = "data:image/jpeg;base64," + base64.replaceAll("\r\n", "");
            return new HashMap<String, String>() {{
                put("codeKey", uuid);
                put("codeImg", captchaBase64);
            }};

        } catch (Exception e) {
            e.fillInStackTrace();
            return null;
        }
    }

    /************************************************************************
     * @author: wg
     * @description: 验证码是否正确
     * @params:
     * @return:
     * @createTime: 13:49  2022/3/21
     * @updateTime: 13:49  2022/3/21
     ************************************************************************/
    @Override
    public boolean validate(String uuid, String code) {
        // 从内存验证
        Boolean verify = verify(uuid, code);

        //删除验证码
        deleteCache(uuid);

        return verify;
    }

    /************************************************************************
     * @author: wg
     * @description:
     * @params:
     * @return:
     * @createTime: 13:52  2022/3/21
     * @updateTime: 13:52  2022/3/21
     ************************************************************************/
    private Boolean verify(String codeKey, String code) {
        final Cache verify = cacheManager.getCache("verify");
        Cache.ValueWrapper valueWrapper = null;
        if (verify != null && codeKey != null) {
            valueWrapper = verify.get(codeKey);
        }
        return valueWrapper != null && valueWrapper.get() != null && Objects.equals(valueWrapper.get(), code);
    }

    private void deleteCache(String codeKey) {
        Cache cache = cacheManager.getCache("verify");
        if (cache != null) {
            cache.clear();
        }
    }
}
