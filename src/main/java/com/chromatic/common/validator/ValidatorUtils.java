/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.common.validator;

import com.chromatic.common.exception.SevenmeException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * hibernate-validator校验工具类
 * <p>
 * 参考文档：http://docs.jboss.org/hibernate/validator/5.4/reference/en-US/html_single/
 *
 * @author Mark sunlightcs@gmail.com
 */
public class ValidatorUtils {
    private static Validator validator;

    static {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * 校验对象
     *
     * @param object 待校验对象
     * @param groups 待校验的组
     * @throws SevenmeException 校验不通过，则报SevenmeException异常
     */
    public static void validateEntity(Object object, Class<?>... groups) throws SevenmeException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            for (ConstraintViolation<Object> constraint : constraintViolations) {
                msg.append(constraint.getMessage()).append("<br>");
            }
            throw new SevenmeException(msg.toString());
        }
    }

    // private static ResourceBundleMessageSource getMessageSource() {
    //     ResourceBundleMessageSource bundleMessageSource = new ResourceBundleMessageSource();
    //     bundleMessageSource.setDefaultEncoding("UTF-8");
    //     bundleMessageSource.setBasenames("i18n/validation", "i18n/validation_common");
    //     return bundleMessageSource;
    // }
    //
    // public static void validateEntity(Object object, Class<?>... groups) throws SevenmeException {
    //     Locale.setDefault(LocaleContextHolder.getLocale());
    //     Validator validator = Validation.byDefaultProvider()
    //             .configure()
    //             .messageInterpolator(new ResourceBundleMessageInterpolator(new MessageSourceResourceBundleLocator(getMessageSource())))
    //             .buildValidatorFactory().getValidator();
    //     Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
    //     if (!constraintViolations.isEmpty()) {
    //         ConstraintViolation<Object> constraint = constraintViolations.iterator().next();
    //         throw new SevenmeException(constraint.getMessage());
    //     }
    // }
}
