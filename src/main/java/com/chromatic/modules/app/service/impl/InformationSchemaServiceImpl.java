package com.chromatic.modules.app.service.impl;

import cn.hutool.core.io.resource.ClassPathResource;
import com.chromatic.common.utils.FileUtil;
import com.chromatic.modules.app.service.InformationSchemaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.chromatic.modules.app.dao.InformationSchemaDao;
import com.chromatic.modules.app.entity.KeyColumnUsage;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InformationSchemaServiceImpl implements InformationSchemaService {

    @Resource
    InformationSchemaDao informationSchemaDao;

    /************************************************************************
     * @description: 根据表名 查询此表的主键是哪些表的外键
     * @author: wg
     * @date: 13:24  2021/11/2
     * @params:
     * @return:
     ************************************************************************/
    public List<KeyColumnUsage> selectInformationSchema(String tableName) {
        List<KeyColumnUsage> keyColumnUsages = informationSchemaDao.selectInformationSchema(tableName);
        keyColumnUsages.forEach(System.out::println);
        return keyColumnUsages;
    }

    @Override
    public List<KeyColumnUsage> readJsonOfInformationSchema(String tableName, ClassPathResource classPathResource) {
        ArrayList<KeyColumnUsage> list = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = FileUtil.readJson(classPathResource);
            JsonNode node = jsonNode.get(tableName);
            if (!ObjectUtils.isEmpty(node) && node.size() > 0) {
                for (int i = 0; i < node.size(); i++) {
                    KeyColumnUsage keyColumnUsage = objectMapper.convertValue(node.get(i), KeyColumnUsage.class);
                    list.add(keyColumnUsage);
                }
                return list;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取数据中所有的表名
     */
    public List<String> getAllTablesName(String datasourceName) {
        return informationSchemaDao.getAllTablesName(datasourceName);
    }

    @Override
    public int deleteData(String tableName, String columnName, String columnVal) {

        return informationSchemaDao.deleteData(tableName, columnName, columnVal);
    }

    public Map<String, List<KeyColumnUsage>> getAllSchemaMap() {
        String datasourceName = "v7138_job_safety_analysis";
        List<String> tableNames = getAllTablesName(datasourceName);
        Map<String, List<KeyColumnUsage>> map = new HashMap<>();
        for (int i = 0; i < tableNames.size(); i++) {
            List<KeyColumnUsage> keyColumnUsageList = informationSchemaDao.selectInformationSchema(tableNames.get(i));
            map.put(tableNames.get(i), keyColumnUsageList);
        }

        return map;
    }

    /************************************************************************
     * @author: wg
     * @description: 将 map 形式数据, 写成 json 文件, 存入指定路径
     * @params:
     * @return:
     * @createTime: 10:06  2022/3/24
     * @updateTime: 10:06  2022/3/24
     ************************************************************************/
    public void writeToJson(Map<String, List<KeyColumnUsage>> allSchema, ClassPathResource classPathResource) {
        // allSchema.forEach((k, v) -> System.out.println(k + ": " + v));
        try {
            ClassPathResource resource = new ClassPathResource("/json/informationSchema.json");
            FileUtil.writeToJson(allSchema, resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
