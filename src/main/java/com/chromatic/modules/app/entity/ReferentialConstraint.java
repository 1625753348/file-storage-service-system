package com.chromatic.modules.app.entity;

/************************************************************************
 * @author: wg
 * @description:
 * @createTime: 10:51 2022/4/13
 * @updateTime: 10:51 2022/4/13
 ************************************************************************/
public class ReferentialConstraint {

    // 数据库
    private String constraintSchema;

    // 约束名
    private String constraintName;

    // 唯一约束模式
    private String uniqueConstraintSchema;

    // 唯一的约束名称
    private String uniqueConstraintName;

    // 匹配选项
    private String matchOption;

    // 更新规则
    private String updateRule;

    // 删除规则
    private String deleteRule;

    // 参照表的 表名 (主表名)
    private String tableName;

    // 被参照的表名
    private String referencedTableName;


    public String getConstraintSchema() {
        return constraintSchema;
    }

    public void setConstraintSchema(String constraintSchema) {
        this.constraintSchema = constraintSchema;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    public String getUniqueConstraintSchema() {
        return uniqueConstraintSchema;
    }

    public void setUniqueConstraintSchema(String uniqueConstraintSchema) {
        this.uniqueConstraintSchema = uniqueConstraintSchema;
    }

    public String getUniqueConstraintName() {
        return uniqueConstraintName;
    }

    public void setUniqueConstraintName(String uniqueConstraintName) {
        this.uniqueConstraintName = uniqueConstraintName;
    }

    public String getMatchOption() {
        return matchOption;
    }

    public void setMatchOption(String matchOption) {
        this.matchOption = matchOption;
    }

    public String getUpdateRule() {
        return updateRule;
    }

    public void setUpdateRule(String updateRule) {
        this.updateRule = updateRule;
    }

    public String getDeleteRule() {
        return deleteRule;
    }

    public void setDeleteRule(String deleteRule) {
        this.deleteRule = deleteRule;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getReferencedTableName() {
        return referencedTableName;
    }

    public void setReferencedTableName(String referencedTableName) {
        this.referencedTableName = referencedTableName;
    }
}
