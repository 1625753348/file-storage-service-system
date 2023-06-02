package com.chromatic.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class CommonUtils {

    @Autowired
    private JwtUtils jj;

    private static CommonUtils commonUtils;

    @PostConstruct
    public void initCommonUtils() {
        commonUtils = this;
    }

    //中文正则
    private static Pattern ZHONGWEN_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");

    /**
     * 判断文件名是否带盘符，重新处理
     *
     * @param fileName
     * @return
     */
    public static String reviseFileName(String fileName) {
        //判断是否带有盘符信息
        // Check for Unix-style path
        int unixSep = fileName.lastIndexOf('/');
        // Check for Windows-style path
        int winSep = fileName.lastIndexOf('\\');
        // Cut off at latest possible point
        int pos = (winSep > unixSep ? winSep : unixSep);
        if (pos != -1) {
            // Any sort of path separator found...
            fileName = fileName.substring(pos + 1);
        }
        //替换上传文件名字的特殊字符
        fileName = fileName.replace("=", "").replace(",", "").replace("&", "")
                .replace("#", "").replace("“", "").replace("”", "");
        //替换上传文件名字中的空格
        fileName = fileName.replaceAll("\\s", "");
        return fileName;
    }

    public static String getUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");
        String userId = null;
        try {
            userId = commonUtils.jj.analyseToken(token).getSubject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token != null ? userId : null;
    }

    /************************************************************************
     * @description: 用于搜索 日期除外
     * @author: wg
     * @date: 13:42  2021/11/11
     * @params:
     * @return:
     ************************************************************************/
    public static Map<String, Object> getParamMap(Map<String, Object> params) {
        Map<String, Object> hashMap = new HashMap<>();
        params.forEach((k, v) -> {
            if (!ObjectUtils.isEmpty(params.get(k))) {
                hashMap.put(humpToLine(k), v);
            }
        });
        return hashMap;
    }

    /************************************************************************
     * @description: 驼峰转下划线
     * @author:
     * @date: 11:22  2021/9/1
     ************************************************************************/
    @SuppressWarnings("AlibabaAvoidPatternCompileInMethod")
    public static String humpToLine(String str) {
        str = str.substring(0, 1).toLowerCase() + str.substring(1);
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
     * @description: 反射 根据字段名, 执行其 get 方法
     * @author: wg
     * @date: 14:27  2021/11/3
     * @params:
     * @return: 返回 get 到的值
     ************************************************************************/
    public static <T> Object getter(String fieldName, T entity) {
        Class<?> aClass = entity.getClass();
        try {
            Field declaredField = aClass.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor((String) declaredField.getName(), aClass);
            Method readMethod = propertyDescriptor.getReadMethod();
            return readMethod.invoke(entity);
        } catch (NoSuchFieldException | IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            return null;
        }
    }

    /************************************************************************
     * @description: 通过反射 set 值
     * @author: wg
     * @date: 13:32  2021/11/8
     * @params:
     * @return:
     ************************************************************************/
    public static <T> void setter(T t, String fieldName, Object attributeValue) {
        try {
            PropertyDescriptor p = new PropertyDescriptor(fieldName, t.getClass());
            Method writeMethod = p.getWriteMethod();
            writeMethod.invoke(t, attributeValue);
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException introspectionException) {
            introspectionException.printStackTrace();
        }
    }

    /************************************************************************
     * @author: wg
     * @description: 反射判断 是否 有这个字段
     * @params:
     * @return:
     * @createTime: 14:37  2022/8/17
     * @updateTime: 14:37  2022/8/17
     ************************************************************************/
    public static <T> boolean  hasField(T t, String fieldName) {
        Class<?> aClass = t.getClass();
        Field[] fields = aClass.getDeclaredFields();
        for (Field f : fields) {
            if (fieldName.equals(f.getName())) {
                return true;
            }
        }
        return false;
    }

}