package com.chromatic.modules.app.service;


import cn.hutool.core.io.resource.ClassPathResource;
import com.chromatic.modules.app.entity.KeyColumnUsage;

import java.util.List;
import java.util.Map;

public interface InformationSchemaService {

    /**
     * 假如数据库中写明了外键关联, 查询 关联关系
     */
    List<KeyColumnUsage> selectInformationSchema(String tableName);

    /**
     * 通过读取 json 文件的形式, 获取数据库中所有表的外键关联
     */
    List<KeyColumnUsage> readJsonOfInformationSchema(String tableName, ClassPathResource classPathResource);

    /**
     * 假如数据库中写明了外键关联, 查询数据库, 获取数据库中所有表的外键关联, 转成 map
     */
    public Map<String, List<KeyColumnUsage>> getAllSchemaMap();

    /**
     * 将数据库中所有表的外键关联的 map 形式 写入 json 文件中
     */
    void writeToJson(Map<String, List<KeyColumnUsage>> allSchema, ClassPathResource classPathResource);

    /**
     * 获取数据库中所有的表名
     */
    public List<String> getAllTablesName(String datasourceName);

    int deleteData(String tableName, String columnName, String columnVal);
}
