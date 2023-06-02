package com.chromatic.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/************************************************************************
 * @author: wg
 * @description:
 * @createTime: 15:38 2022/6/8
 * @updateTime: 15:38 2022/6/8
 ************************************************************************/
public class StringUtil {

    /************************************************************************
     * @author: wg
     * @description: 在数字前面加 n 个 0
     * @params:
     * @return:
     * @createTime: 15:35  2022/6/8
     * @updateTime: 15:35  2022/6/8
     ************************************************************************/
    public static String paddingZero(int numeral, int length) {
        String codeFormat = "%0" + String.valueOf(length) + "d"; // %04d

        return String.format(codeFormat, numeral);
    }

    /************************************************************************
     * @description: 驼峰转下划线
     * @author:
     * @date: 11:22  2021/9/1
     ************************************************************************/
    @SuppressWarnings("AlibabaAvoidPatternCompileInMethod")
    public static String humpToLine(String str) {
        Pattern humpPattern = Pattern.compile("[A-Z]");
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    /************************************************************************
     * @description: 下划线转驼峰
     * @author: wg
     * @date: 13:51  2021/11/11
     * @params:
     * @return:
     ************************************************************************/
    public static String getHumpString(String str) {
        String[] s = str.split("_");
        StringBuilder stringBuilder = new StringBuilder(s[0]);
        for (int k = 0; k < s.length - 1; k++) {
            stringBuilder.append(s[k + 1].substring(0, 1).toUpperCase()).append(s[k + 1].substring(1));
        }

        return stringBuilder.toString();
    }
}
