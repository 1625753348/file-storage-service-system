

package com.chromatic.common.mybatis.annotation;

import java.lang.annotation.*;

/**
 * 数据过滤注解
 *
 * @author Seven ME info@7-me.net
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataFilter {
    /**
     * 表的别名
     */
    String tableAlias() default "";

    /**
     * 用户ID
     */
    String userId() default "creator";

    /**
     * 部门ID
     */
    String deptId() default "dept_id";

}