

package com.chromatic.common.mybatis.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.chromatic.common.exception.SevenmeException;
import com.chromatic.common.mybatis.service.BaseService;
import com.chromatic.common.enums.OperatorEnum;
import com.chromatic.common.mybatis.utils.EntityUtils;
import com.chromatic.common.utils.Constant;
import com.chromatic.common.utils.ConvertUtils;
import com.chromatic.common.vo.PageData;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * 基础服务类，所有Service都要继承
 *
 * @author Seven ME info@7-me.net
 * @since 1.0.0
 */
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T> implements BaseService<T> {
    @Autowired
    protected M baseDao;

    /**
     * 获取分页对象
     *
     * @param params            分页查询参数
     * @param defaultOrderField 默认排序字段
     * @param isAsc             排序方式
     */
    protected IPage<T> getPage(Map<String, Object> params, String defaultOrderField, boolean isAsc) {
        //分页参数
        long curPage = 1;
        long limit = 10;

        if (params.get(Constant.PAGE) != null) {
            curPage = Long.parseLong((String) params.get(Constant.PAGE));
        }
        if (params.get(Constant.LIMIT) != null) {
            limit = Long.parseLong((String) params.get(Constant.LIMIT));
        }

        //分页对象
        Page<T> page = new Page<>(curPage, limit);

        //分页参数
        params.put(Constant.PAGE, page);

        //排序字段
        String orderField = (String) params.get(Constant.ORDER_FIELD);
        String order = (String) params.get(Constant.ORDER);

        //前端字段排序
        if (StringUtils.isNotBlank(orderField) && StringUtils.isNotBlank(order)) {
            if (Constant.ASC.equalsIgnoreCase(order)) {
                return page.addOrder(OrderItem.asc(orderField));
            } else {
                return page.addOrder(OrderItem.desc(orderField));
            }
        }

        //没有排序字段，则不排序
        if (StringUtils.isBlank(defaultOrderField)) {
            return page;
        }

        //默认排序
        if (isAsc) {
            page.addOrder(OrderItem.asc(defaultOrderField));
        } else {
            page.addOrder(OrderItem.desc(defaultOrderField));
        }

        return page;
    }

    protected <T> PageData<T> getPageData(List<?> list, long total, Class<T> target) {
        List<T> targetList = ConvertUtils.sourceToTarget(list, target);

        return new PageData<>(targetList, total);
    }

    protected <T> PageData<T> getPageData(IPage page, Class<T> target) {
        return getPageData(page.getRecords(), page.getTotal(), target);
    }

    /*****************************************************
     * @params:
     * @description: 有查询字段就条件查询, 没有就直接查询
     * @author: wg
     * @date: 2021/6/30 17:01
     *****************************************************/
    protected QueryWrapper<T> getWrapper(Map<String, Object> params) {
        return new QueryWrapper<>();
    }

    private boolean getSingleObj(Object obj, OperatorEnum operator) {
        if (obj instanceof String || obj instanceof Integer || obj instanceof Double) {
            return true;
        } else {
            throw new SevenmeException(operator.name() + "条件数据类型错误,应为string或number");
        }
    }

    private boolean getArrayObj(Object obj, OperatorEnum operator) {
        if (obj instanceof ArrayList) {
            return true;
        } else {
            throw new SevenmeException(operator.name() + "条件数据类型错误,应为array");
        }
    }

    private boolean getDoubleObj(Object obj, OperatorEnum operator) {
        if (obj instanceof ArrayList) {
            List objs = (ArrayList) obj;
            if (objs.size() == 2) {
                return true;
            }
        }
        throw new SevenmeException(operator.name() + "条件数据类型错误,应为array;或者参数数量错误,参数只能为2");
    }

    protected Map<String, Object> paramsToLike(Map<String, Object> params, String... likes) {
        for (String like : likes) {
            String val = (String) params.get(like);
            if (StringUtils.isNotBlank(val)) {
                params.put(like, "%" + val + "%");
            } else {
                params.put(like, null);
            }
        }
        return params;
    }

    /**
     * 逻辑删除
     *
     * @param ids    ids
     * @param entity 实体
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean logicDelete(Long[] ids, Class<T> entity) {
        List<T> entityList = EntityUtils.deletedBy(ids, entity);

        return updateBatchById(entityList);
    }

    /**
     * <p>
     * 判断数据库操作是否成功
     * </p>
     * <p>
     * 注意！！ 该方法为 Integer 判断，不可传入 int 基本类型
     * </p>
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    protected static boolean retBool(Integer result) {
        return SqlHelper.retBool(result);
    }

    protected Class<T> currentModelClass() {
        return (Class<T>) ReflectionKit.getSuperClassGenericType(getClass(), 1);
    }

    /**
     * <p>
     * 批量操作 SqlSession
     * </p>
     */
    protected SqlSession sqlSessionBatch() {
        return SqlHelper.sqlSessionBatch(currentModelClass());
    }

    /**
     * 释放sqlSession
     *
     * @param sqlSession session
     */
    protected void closeSqlSession(SqlSession sqlSession) {
        SqlSessionUtils.closeSqlSession(sqlSession, GlobalConfigUtils.currentSessionFactory(currentModelClass()));
    }

    /**
     * 获取SqlStatement
     *
     * @param sqlMethod
     * @return
     */
    protected String sqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.table(currentModelClass()).getSqlStatement(sqlMethod.getMethod());
    }

    @Override
    public boolean insert(T entity) {
        return BaseServiceImpl.retBool(baseDao.insert(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<T> entityList) {
        return insertBatch(entityList, 100);
    }

    /**
     * 批量插入
     *
     * @param entityList
     * @param batchSize
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<T> entityList, int batchSize) {
        SqlSession batchSqlSession = sqlSessionBatch();
        int i = 0;
        String sqlStatement = sqlStatement(SqlMethod.INSERT_ONE);
        try {
            for (T anEntityList : entityList) {
                batchSqlSession.insert(sqlStatement, anEntityList);
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        } finally {
            closeSqlSession(batchSqlSession);
        }
        return true;
    }

    @Override
    public boolean updateById(T entity) {
        return BaseServiceImpl.retBool(baseDao.updateById(entity));
    }

    @Override
    public boolean update(T entity, Wrapper<T> updateWrapper) {
        return BaseServiceImpl.retBool(baseDao.update(entity, updateWrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<T> entityList) {
        return updateBatchById(entityList, 30);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            throw new IllegalArgumentException("Error: entityList must not be empty");
        }
        SqlSession batchSqlSession = sqlSessionBatch();
        int i = 0;
        String sqlStatement = sqlStatement(SqlMethod.UPDATE_BY_ID);
        try {
            for (T anEntityList : entityList) {
                MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                param.put(Constants.ENTITY, anEntityList);
                batchSqlSession.update(sqlStatement, param);
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        } finally {
            closeSqlSession(batchSqlSession);
        }
        return true;
    }

    @Override
    public T selectById(Serializable id) {
        return baseDao.selectById(id);
    }

    @Override
    public boolean deleteById(Serializable id) {
        return SqlHelper.retBool(baseDao.deleteById(id));
    }

    @Override
    public boolean deleteBatchIds(Collection<? extends Serializable> idList) {
        return SqlHelper.retBool(baseDao.deleteBatchIds(idList));
    }

    /*****************************************************
     * @params:
     * @description: 获取搜索map, 获取的是其他表的字段参数,
     * @author: wg
     * @date: 2021/7/8 15:12
     *****************************************************/
    public HashMap<String, Object> getSearchMap(Map<String, Object> params) {
        HashMap<String, Object> searchMap = new HashMap<>();
        String pipelineNumber = (String) params.get("pipelineNumber");
        String pipelineName = (String) params.get("pipelineName");
        String pipelineProduct = (String) params.get("pipelineProduct"); // 介质类型
        String pipelineLocation = (String) params.get("pipelineLocation"); // 位置类型

        if (StringUtils.isNotBlank(pipelineName)) {
            searchMap.put("pipeline_name", pipelineName.trim());
        }

        if (StringUtils.isNotBlank(pipelineNumber)) {
            searchMap.put("pipeline_number", pipelineNumber.trim());
        }

        // 介质类型
        if (org.apache.commons.lang3.StringUtils.isNotBlank(pipelineProduct)) {
            searchMap.put("pipeline_product", pipelineProduct.trim());
        }

        // 位置类型
        if (org.apache.commons.lang3.StringUtils.isNotBlank(pipelineLocation)) {
            searchMap.put("pipeline_location", pipelineLocation.trim());
        }

        return searchMap;
    }


}