/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.common.utils;

import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Map工具类
 *
 * @author Mark sunlightcs@gmail.com
 */
public class MapUtils extends HashMap<String, Object> {

    @Override
    public MapUtils put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /************************************************************************
     * @author: wg
     * @description: 判断 map 的值 是否都为空
     * @params:
     * @return:
     * @createTime: 14:46  2022/4/14
     * @updateTime: 14:46  2022/4/14
     ************************************************************************/
    public static boolean isAllEmptyValue(Map<String, Object> params) {
        if (params == null || params.size() == 0) {
            return true;
        }
        boolean b = false;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (ObjectUtils.isEmpty(entry.getValue())) {
                b = true;
            } else {
                return false;
            }
        }
        return b;
    }

    public static <T> boolean allEmptyValue(Map<Long, T> params) {
        if (params == null || params.size() == 0) {
            return true;
        }

        boolean b = false;
        for (Map.Entry<Long, T> entry : params.entrySet()) {
            if (ObjectUtils.isEmpty(entry.getValue())) {
                b = true;
            } else {
                return false;
            }
        }
        return b;
    }
}
