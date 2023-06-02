package com.chromatic.modules.app.dao;

import com.chromatic.modules.app.entity.ReferentialConstraint;
import com.chromatic.common.mybatis.dao.BaseDao;
import com.chromatic.modules.app.entity.KeyColumnUsage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InformationSchemaDao extends BaseDao<KeyColumnUsage> {

    List<KeyColumnUsage> selectInformationSchema(String masterTableName);

    KeyColumnUsage selectKeyColumnUsageBy(String masterTableName, String sonTableName, String constraintName);

    int deleteData(String tableName, String columnName, String columnVal);

    int getDataCount(String tableName, String columnName, String masterTableId);

    List<String> getIds(String tableName, String columnName, String masterTableId);

    List<String> getAllTablesName(String datasourceName);

    List<ReferentialConstraint> selectReferentialConstraints(String tableName);

    int logicDeleteData(String tableName, String columnName, String columnVal);

    String selectTableComment(String tableName);

    List<String> selectTableField(String tableName);
}
