package com.chromatic.common.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.chromatic.modules.sys.entity.SysUserEntity;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * 公共字段，自动填充值
 *
 * @author Seven ME info@7-me.net
 * 机制比较诡异，对于原有值为null的字段进行填充，不为null则不填充，所以需要将填充字段提前置空，但是mybatis的机制是entity中为null值不更新，所以只能在dto转换时
 */
@Component
public class FieldMetaObjectHandler implements MetaObjectHandler {
    private final static String CREATE_TIME = "createTime";
    private final static String UPDATE_TIME = "updateTime";
    private final static String CREATE_USER = "createUser";
    private final static String UPDATE_USER = "updateUser";

    @Override
    public void insertFill(MetaObject metaObject) {
        Date date = new Date();
        //final String user = CommonUtils.getUser();
        String username = ((SysUserEntity) SecurityUtils.getSubject().getPrincipal()).getName();
        strictInsertFill(metaObject, CREATE_TIME, Date.class, date); // 没起作用, 可能是版本问题
        strictInsertFill(metaObject, CREATE_USER, String.class, username); // 没起作用, 可能是版本问题
        strictInsertFill(metaObject, UPDATE_TIME, Date.class, date);
        strictInsertFill(metaObject, UPDATE_USER, String.class, username);

        setFieldValByName(CREATE_TIME, date, metaObject);
        setFieldValByName(CREATE_USER, username, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String username = ((SysUserEntity) SecurityUtils.getSubject().getPrincipal()).getName();
        strictUpdateFill(metaObject, UPDATE_TIME, Date.class, new Date());
        strictUpdateFill(metaObject, UPDATE_USER, String.class, username);
    }

}