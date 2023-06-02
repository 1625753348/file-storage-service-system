

package com.chromatic.common.annotation.app;

import java.lang.annotation.*;

/**
 * app登录效验
 *
 * @author Mark sunlightcs@gmail.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Login {
}
