package com.chromatic.common.mybatis.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.chromatic.common.utils.StringUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.logging.jdbc.PreparedStatementLogger;
import org.apache.ibatis.plugin.*;

@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = { Statement.class })
})
public class DictEchoInterceptor implements Interceptor {
//    private CommonDictService commonDictService ;
//
//    public DictEchoInterceptor(CommonDictService commonDictService) {
//        this.commonDictService = commonDictService;
//    }
//
//
//    @Override
//    public Object intercept(Invocation invocation) throws Throwable {
//        // 进行相应的服务操作
//        Object result = invocation.proceed();
//        // 判断返回值是否是 List，是否非空，是否元素类型是 FacilityEntity 类
//        //&& ((List<?>) result).get(0) instanceof FacilityEntity
//        if (result instanceof List && !((List<?>) result).isEmpty() ) {
//            List<?> resultList = (List<?>) result;
//            //获取表名
//            Set<String> tableNames = new HashSet<>();
//            if (invocation.getArgs() != null && invocation.getArgs().length > 0  ) {
//                Statement stmt = (Statement) invocation.getArgs()[0];
//                // 获取Statement执行的SQL，并得到表名
//                PreparedStatementLogger logger = (PreparedStatementLogger) Proxy.getInvocationHandler(stmt);
//                DruidPooledPreparedStatement dpstmt = (DruidPooledPreparedStatement)logger.getPreparedStatement();
//                String sql = dpstmt.getSql();
//                tableNames = extractTableNames(sql);// 获取结果集的第一个表名
//            }
//            if (StringUtils.isBlank(tableNames.iterator().next())) {return result;}
//            //根据表名找要回显的字段
//            //如果没返回结果null
//            Map<String, List<CommonDictEntity>> colNameByTableName = commonDictService.getColNameByTableName(tableNames.iterator().next());
//            if (colNameByTableName == null){return result;}
//            Map<String,Map<String,String>> colNameKV = getColCodeText(colNameByTableName);
//
//            //回显要实现1、拿要回显的字段名 2、拿到字段的值 3、与kv映射 4、填入回显echoText字段中
//            resultList.stream().forEach(e->{
//                for (Map.Entry<String, Map<String, String>> entry : colNameKV.entrySet()) {
//                    String colName = entry.getKey();
//                    String code = String.valueOf(getIntValue(e, colName));
//                    for (Map.Entry<String, String> innerEntry : entry.getValue().entrySet()) {
//                        if (code.equals(innerEntry.getKey())) {
//                            setEchoText(e,innerEntry.getValue());
//                        }
//                    }
//                }
//            });
//
//            //查到所有的字段名，
//       //     String methodName  = "set"+colNameByTableName.entrySet().iterator().next().getKey();
////            resultList.stream().forEach(e->{
////                //int dictCode = getIntValue(e.getClass(), "");
////                try {
////                    e.getClass().getMethod("setOperatingStatus", Integer.class).invoke(e, 12);
////                } catch (NoSuchMethodException ex) {
////                    throw new RuntimeException(ex);
////                } catch (InvocationTargetException ex) {
////                    throw new RuntimeException(ex);
////                } catch (IllegalAccessException ex) {
////                    throw new RuntimeException(ex);
////                }
////
////            });
//        }
//        return result;
//    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return null;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 不需要配置任何属性
    }

    /**
     * 获取sql里的表名
     * @param sql
     * @return
     */
    public Set<String> extractTableNames(String sql) {
        Set<String> tableNames = new HashSet<>();
        String regex = "\\bFROM\\b\\s+(\\w+)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            String tableName = matcher.group(1);
            tableNames.add(tableName);
        }
        return tableNames;
    }

    public static void setEchoText(Object obj, String echoText){
        Class<?> clazz = obj.getClass();
        Field nameField = null;
        try {
            nameField = clazz.getDeclaredField("echoText");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        nameField.setAccessible(true);
        try {
            nameField.set(obj, echoText);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取对象的某个属性的值
     * @param obj
     * @param fieldName
     * @return
     */
    public static int getIntValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(StringUtil.getHumpString(fieldName));
            field.setAccessible(true);
            return (int) field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 转换一下集合的结构
     * @param resultList
     * @return
     */
//    private Map<String,Map<String,String>> getColCodeText(Map<String, List<CommonDictEntity>> resultList){
//        Map<String,Map<String,String>> r = new HashMap<>();
//        for (Map.Entry<String, List<CommonDictEntity>> stringListEntry : resultList.entrySet()) {
//            Map<String,String> kv = new HashMap<>();
//            stringListEntry.getValue().stream().forEach(e->{
//                kv.put(String.valueOf(e.getValue()),e.getText());
//            });
//            r.put(stringListEntry.getKey(),kv);
//        }
//        return  r;
//    }
}
