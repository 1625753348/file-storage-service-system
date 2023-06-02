package com.chromatic.common.mybatis.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE_USE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Dict {

    String tableName();
    String colName();
}
