package com.chromatic.modules.app.dao;

import org.apache.ibatis.annotations.Mapper;

/************************************************************************
 * @author: wg
 * @description:
 * @createTime: 16:41 2022/6/1
 * @updateTime: 16:41 2022/6/1
 ************************************************************************/
@Mapper
public interface CommonDao {

    Long selectByTable(String tableName, Long tableId);

    Long selectSpRepair(String tableName, Long tableId);
}
