package com.chromatic.modules.app.entity;


public class KeyColumnUsage {

    // 参照表的 表名
    private String tableName;
    // 表里的列名
    private String columnName;
    // 被参照的表名
    private String referencedTableName;
    // 被参照的列名
    private String referencedColumnName;
    // 约束名
    private String constraintName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getReferencedTableName() {
        return referencedTableName;
    }

    public void setReferencedTableName(String referencedTableName) {
        this.referencedTableName = referencedTableName;
    }

    public String getReferencedColumnName() {
        return referencedColumnName;
    }

    public void setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    @Override
    public String toString() {
        return "InformationSchema{" +
                "tableName='" + tableName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", referencedTableName='" + referencedTableName + '\'' +
                ", referencedColumnName='" + referencedColumnName + '\'' +
                ", constraintName='" + constraintName + '\'' +
                '}';
    }
}
