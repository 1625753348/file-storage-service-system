/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.common.exception;

import com.chromatic.common.vo.Result;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 异常处理器
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestControllerAdvice
public class SevenmeExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(SevenmeException.class)
    public Result handleSevenmeException(SevenmeException e) {
        Result result = new Result();
        result.setCode(e.getCode());
        result.setMsg(e.getMessage());
        return result;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Result handlerNoFoundException(Exception e) {
        logger.error(e.getMessage(), e);
        Result result = new Result();
        return result.error(404, "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException e) {
        logger.error(e.getMessage(), e);
        Result result = new Result();
        return result.error("数据库中已存在该记录");
    }

    @ExceptionHandler(AuthorizationException.class)
    public Result handleAuthorizationException(AuthorizationException e) {
        logger.error(e.getMessage(), e);
        Result result = new Result();
        return result.error("没有权限，请联系管理员授权");
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        logger.error(e.getMessage(), e);
        Result result = new Result();
        return result.error("未知异常");
    }

    @ExceptionHandler(java.sql.SQLIntegrityConstraintViolationException.class)
    public Result handleSqlException(SQLIntegrityConstraintViolationException e) {
        logger.error(e.getMessage(), e);
        Result result = new Result();
        return result.error("sql异常");
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public Result handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        logger.error(e.getMessage(), e);
        Result result = new Result();
        return result.error("数据库唯一键异常");
    }

    @ExceptionHandler(java.lang.NullPointerException.class)
    public Result handleNullPointerException(NullPointerException e) {
        logger.error(e.getMessage(), e);
        Result result = new Result();
        return result.error("空指针异常");
    }
}
