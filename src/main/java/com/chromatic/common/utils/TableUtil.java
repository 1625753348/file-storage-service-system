package com.chromatic.common.utils;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.chromatic.common.exception.SevenmeException;
import com.chromatic.modules.app.dao.InformationSchemaDao;
import com.chromatic.modules.app.entity.KeyColumnUsage;
import com.chromatic.modules.app.entity.ReferentialConstraint;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.util.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Component
public class TableUtil {

    private static final String CONSTRAINT_SCHEMA = "v7138_job_safety_analysis";

    @Resource
    private InformationSchemaDao informationSchemaDao;

    public static final Set<String> tables = new HashSet<String>();

    public static TableUtil tableUtil;

    @PostConstruct
    public void init() {
        tableUtil = this;
    }

    /************************************************************************
     * @description: 查询 此表 与之 关联的所有表
     * @author: wg
     * @date: 14:36  2021/11/26
     * @params:
     * @return: 获取 所有关联表的 表名
     ************************************************************************/
    public static Set<String> getTable(String tableName) {
        List<KeyColumnUsage> keyColumnUsages = tableUtil.informationSchemaDao.selectInformationSchema(tableName);
        if (keyColumnUsages.size() > 0) {
            for (KeyColumnUsage keyColumnUsage : keyColumnUsages) {
                tableName = keyColumnUsage.getTableName();
                tables.add(tableName);
                getTable(tableName);
            }
        }
        return tables;
    }

    /************************************************************************
     * @description:
     * 外键关联形式
     * 获取此id关联的有数据的关联表; 查询 此表 中 某条数据 与之 关联的所有其他表中有数据的表
     * @author: wg
     * @date: 9:38  2021/12/1
     * @params:
     * @return:
     ************************************************************************/
    public static List<String> getRelation(String masterTableName, String masterTableId) {
        ArrayList<String> tableNames = new ArrayList<>();
        String tableName = "";
        String columnName = "";
        List<KeyColumnUsage> keyColumnUsages = tableUtil.informationSchemaDao.selectInformationSchema(masterTableName);
        for (KeyColumnUsage keyColumnUsage : keyColumnUsages) {
            tableName = keyColumnUsage.getTableName();
            columnName = keyColumnUsage.getColumnName();
            int size = tableUtil.informationSchemaDao.getDataCount(tableName, columnName, masterTableId);
            if (size != 0) {
                tableNames.add(tableName);
            }
        }
        return tableNames;
    }

    /************************************************************************
     * @description:
     * json 形式
     * 获取此id关联的有数据的关联表; 查询 此表 中 某条数据 与之 关联的所有其他表中有数据的表
     * @author: wg
     * @date: 10:47  2021/12/1
     * @params:
     * @return:
     ************************************************************************/
    public static List<String> getInformationSchema(String masterTableName, String masterTableId) {
        ArrayList<String> tableNames = new ArrayList<>();
        JSONObject informationSchemaJson = getJsonByTableName(masterTableName);
        if (informationSchemaJson != null) {
            JSONArray tableNameJson = informationSchemaJson.getJSONArray("TABLE_NAME");
            String columnName = informationSchemaJson.getStr("COLUMN_NAME");
            for (Object tableName : tableNameJson) {
                int size = tableUtil.informationSchemaDao.getDataCount(tableName.toString(), columnName, masterTableId);
                if (size != 0) {
                    tableNames.add(tableName.toString());
                }
            }
            return tableNames;
        }
        return tableNames;
    }

    public static JSONObject getJsonByTableName(String tableName) {
        ClassPathResource resource = new ClassPathResource("json/informationSchema.json");
        File file = resource.getFile();
        if (file.exists()) {
            try {
                String jsonStr = FileUtils.readFile(new FileInputStream(file));

                JSONObject jsonObject = new JSONObject(jsonStr);
                String informationSchema = jsonObject.getStr(tableName);

                JSONObject informationSchemaJson = new JSONObject(informationSchema);

                return informationSchemaJson;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // public static int count;
    public static int step;

    /************************************************************************
     * @description: 删除 与 主表 关联 的 所有表的 数据 , 感觉 成功了
     * @author: wg
     * @date: 16:20  2021/11/30
     * @params:
     * masterTableName = section_layout_history
     * masterTableId = section_layout_history.id (9e0e63662d114d15940e0ea4f7a68617)
     * @return:
     ************************************************************************/
    public static int deleteAllRelation(String masterTableName, String masterTableId) {
        // 记录递归次数
        int count = 1;
        String tableName = "";
        String columnName = "";
        // 删除数据的总条数
        int deleteCount = 0;

        // 读取数据库数据
        List<KeyColumnUsage> keyColumnUsages = tableUtil.informationSchemaDao.selectInformationSchema(masterTableName);

        if (keyColumnUsages.size() > 0) {
            for (KeyColumnUsage keyColumnUsage : keyColumnUsages) {
                tableName = keyColumnUsage.getTableName();
                columnName = keyColumnUsage.getColumnName();
                List<String> ids = tableUtil.informationSchemaDao.getIds(tableName, columnName, masterTableId);
                if (ids.size() > 0) {
                    List<KeyColumnUsage> schemaList = tableUtil.informationSchemaDao.selectInformationSchema(tableName);
                    // 如果 schemaList.size() > 0 说明有多级关联
                    if (schemaList.size() == 0) {
                        // count++;
                        System.out.println("第 " + count + " 次删除, 删除的是 " + tableName + " 里面的相关数据");
                        //  delete from api5792007_detail where corrosion_assessment_history_id = #{masterTableId}
                        deleteCount += tableUtil.informationSchemaDao.deleteData(tableName, columnName, masterTableId);
                        // count = 0;
                    } else {
                        for (String id : ids) {
                            count++;
                            deleteAllRelation(tableName, id);
                        }
                    }
                }
            }
        }

        return deleteCount;
    }

    /************************************************************************
     * @author: wg
     * @description: 超出 stepOrder 级关联后不让删;
     * @params:
     * @return:
     * @createTime: 16:21  2022/3/25
     * @updateTime: 16:21  2022/3/25
     ************************************************************************/
    public static void deleteAllRelationByStepOrder(String masterTableName, String masterTableId, int stepOrder) {
        int count = 0;
        String tableName = "";
        String columnName = "";

        // 读取数据库数据
        // List<InformationSchema> informationSchemas = tableUtil.informationSchemaDao.selectInformationSchema(masterTableName);

        // 读取json文件里的数据
        List<KeyColumnUsage> keyColumnUsages = readJsonOfInformationSchema(masterTableName, new ClassPathResource("/json/informationSchema.json"));
        if (keyColumnUsages.size() > 0) {
            for (KeyColumnUsage keyColumnUsage : keyColumnUsages) {
                tableName = keyColumnUsage.getTableName();
                columnName = keyColumnUsage.getColumnName();
                List<String> ids = tableUtil.informationSchemaDao.getIds(tableName, columnName, masterTableId);
                if (ids.size() > 0) {
                    step++;
                    if (step == stepOrder) {
                        System.out.println("删除, 超过 " + stepOrder + " 次 关联, 给出提示 --- ");
                        return;
                    }
                    List<KeyColumnUsage> schemaList = tableUtil.informationSchemaDao.selectInformationSchema(tableName);
                    if (schemaList.size() == 0) {
                        count++;
                        System.out.println("ByStepOrder 第 " + count + " 次删除, 删除的是 " + tableName + " 里面的相关数据");
                        //  delete from api5792007_detail where corrosion_assessment_history_id = #{masterTableId}
                        tableUtil.informationSchemaDao.deleteData(tableName, columnName, masterTableId);
                    } else {
                        for (String id : ids) {
                            deleteAllRelation(tableName, id);
                            // deleteAllRelationByStepOrder(tableName, id, stepOrder);
                        }
                    }
                }
            }
        }
    }

    /************************************************************************
     * @author: wg
     * @description: 读取指定位置的json文件里的 指定数据
     * @params:
     * @return:
     * @createTime: 15:59  2022/3/25
     * @updateTime: 15:59  2022/3/25
     ************************************************************************/
    public static List<KeyColumnUsage> readJsonOfInformationSchema(String tableName, ClassPathResource classPathResource) {
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

    /************************************************************************
     * @author: wg
     * @description: 查询 键的属性
     * @params:
     * @return:
     * @createTime: 14:07  2022/4/13
     * @updateTime: 14:07  2022/4/13
     ************************************************************************/
    public static Map<String, List<ReferentialConstraint>> selectReferentialConstraints(String tableName) {
        Map<String, List<ReferentialConstraint>> map = new HashMap();
        List<ReferentialConstraint> restrictList = new ArrayList<>();
        List<ReferentialConstraint> cascadeList = new ArrayList<>();
        List<ReferentialConstraint> referentialConstraintList = tableUtil.informationSchemaDao.selectReferentialConstraints(tableName);
        if (referentialConstraintList.size() > 0) {
            String deleteRule = "";
            for (ReferentialConstraint referentialConstraint : referentialConstraintList) {
                deleteRule = referentialConstraint.getDeleteRule();
                if ("RESTRICT".equals(deleteRule)) {
                    restrictList.add(referentialConstraint);
                }
                if ("CASCADE".equals(deleteRule)) {
                    cascadeList.add(referentialConstraint);
                }
            }
            if (restrictList.size() > 0) {
                map.put("RESTRICT", restrictList);
            }
            if (cascadeList.size() > 0) {
                map.put("CASCADE", cascadeList);
            }
        }

        return map;
    }

    /************************************************************************
     * @author: wg
     * @description: tableName = sp_basic; masterTableName = common_branch ;
     * @params:
     * @return:
     * @createTime: 15:36  2022/4/13
     * @updateTime: 15:36  2022/4/13
     ************************************************************************/
    public static boolean catchTable(String tableName, String masterTableName) {
        Map<String, List<ReferentialConstraint>> map = selectReferentialConstraints(tableName);
        if (!map.isEmpty()) {
            if (map.containsKey("CASCADE")) {
                List<ReferentialConstraint> cascadeList = map.get("CASCADE");
                for (ReferentialConstraint referentialConstraint : cascadeList) {
                    if (masterTableName.equals(referentialConstraint.getReferencedTableName())) {
                        return true;
                    }
                }
            }
            if (map.containsKey("RESTRICT")) {
                List<ReferentialConstraint> restrictList = map.get("RESTRICT");
                for (ReferentialConstraint referentialConstraint : restrictList) {
                    if (masterTableName.equals(referentialConstraint.getReferencedTableName())) {
                        return false;
                    }
                }
                return false;
            }
            return false;
        }
        return true;
    }

    /************************************************************************
     * @author: wg
     * @description: 逻辑删除 所有的关联数据
     * @params:
     * @return:
     * @createTime: 15:23  2022/4/13
     * @updateTime: 15:23  2022/4/13
     ************************************************************************/
//    public static int logicDeleteAllRelation(String masterTableName, String masterTableId) {
//        // 记录递归次数
//        int count = 1;
//        String tableName = "";
//        String columnName = "";
//        // 删除数据的总条数
//        int deleteCount = 0;
//
//        // 读取数据库外键关联
//        List<KeyColumnUsage> keyColumnUsages = tableUtil.informationSchemaDao.selectInformationSchema(masterTableName);
//
//        if (keyColumnUsages.size() > 0) {
//            for (KeyColumnUsage keyColumnUsage : keyColumnUsages) {
//                tableName = keyColumnUsage.getTableName();
//                columnName = keyColumnUsage.getColumnName();
//                List<String> ids = tableUtil.informationSchemaDao.getIds(tableName, columnName, masterTableId);
//                // 1. 先判断表里是否有相关数据, 如果有相关数据, 再判断是否能删
//                if (ids.size() > 0) {
//                    List<KeyColumnUsage> schemaList = tableUtil.informationSchemaDao.selectInformationSchema(tableName);
//                    // 2. 如果 schemaList.size() > 0 说明有多级关联
//                    if (schemaList.size() == 0) {
//                        // 3. 查询 外键删除规则, 如果能删 则 删除
//                        if (catchTable(tableName, masterTableName)) {
//                            // count++;
//                            System.out.println("第 " + count + " 次删除, 删除的是 " + tableName + " 里面的相关数据");
//                            //  delete from api5792007_detail where corrosion_assessment_history_id = #{masterTableId}
//                            deleteCount += tableUtil.informationSchemaDao.logicDeleteData(tableName, columnName, masterTableId);
//                            // count = 0;
//                        } else {
//                            throw new SevenmeException("因有级联数据, 所以无法删除， 请先删除关联表的相关数据");
//                        }
//                    } else {
//                        for (String id : ids) {
//                            count++;
//                            deleteAllRelation(tableName, id);
//                        }
//                    }
//                }
//            }
//        }
//
//        Map<String, Integer> map = new HashMap<>();
//        map.put("递归次数", count);
//        map.put("删除条数", deleteCount);
//
//        return deleteCount;
//    }
    public static int logicDeleteAllRelation(String masterTableName, String masterTableId) {
        // 记录递归次数
        int count = 1;
        String tableName = "";
        String columnName = "";
        // 删除数据的总条数
        int deleteCount = 0;
        int i=0;
        String tableComment = "";
        List<String> tableContextList = new ArrayList<String>();

        // 读取数据库外键关联
        List<KeyColumnUsage> keyColumnUsages = tableUtil.informationSchemaDao.selectInformationSchema(masterTableName);

        if (keyColumnUsages.size() > 0) {
            for (KeyColumnUsage keyColumnUsage : keyColumnUsages) {
                tableName = keyColumnUsage.getTableName();
                columnName = keyColumnUsage.getColumnName();
                List<String> strings = tableUtil.informationSchemaDao.selectTableField(tableName);
                if (!strings.contains("del_flag")||tableName.equals("job_type_template")||tableName.equals("job_measure_model")){
                    i+= tableUtil.informationSchemaDao.deleteData(tableName, columnName, masterTableId);
                    System.out.println(" 删除" + tableName + " 里面的相关数"+i+"条");

                    continue;
                }
                List<String> ids = tableUtil.informationSchemaDao.getIds(tableName, columnName, masterTableId);
                // 1. 先判断表里是否有相关数据, 如果有相关数据, 再判断是否能删
                if (ids.size() > 0) {
                    tableComment = tableUtil.informationSchemaDao.selectTableComment(tableName);
                    tableContextList.add(tableComment);

                    List<KeyColumnUsage> schemaList = tableUtil.informationSchemaDao.selectInformationSchema(tableName);
                    // 2. 如果 schemaList.size() > 0 说明有多级关联
                    if (schemaList.size() == 0) {
                        // 3. 查询 外键删除规则, 如果能删 则 删除
                        if (catchTable(tableName, masterTableName)) {
                            // count++;
                            System.out.println("第 " + count + " 次删除, 删除的是 " + tableName + " 里面的相关数据");
                            //  delete from api5792007_detail where corrosion_assessment_history_id = #{masterTableId}
                            deleteCount += tableUtil.informationSchemaDao.logicDeleteData(tableName, columnName, masterTableId);
                            // count = 0;
                        } else {
                            continue;
                        }
                    } else {
                        for (String id : ids) {
                            count++;
                            logicDeleteAllRelation(tableName, id);
                        }
                    }
                }
            }
        }

        if (tableContextList.size() > 0) {
            throw new SevenmeException("如果确定要删除此条数据，请先删除这些表的相关数据: " + tableContextList);
        }

        Map<String, Integer> map = new HashMap<>();
        map.put("递归次数", count);
        map.put("删除条数", deleteCount);

        return deleteCount;
    }
}
