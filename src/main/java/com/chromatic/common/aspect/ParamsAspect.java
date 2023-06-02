package com.chromatic.common.aspect;

import com.chromatic.modules.sys.entity.SysUserEntity;
import com.chromatic.common.utils.CommonUtils;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/************************************************************************
 * @author: wg
 * @description:
 * @createTime: 13:52 2022/9/19
 * @updateTime: 13:52 2022/9/19
 ************************************************************************/
@Aspect
@Component
public class ParamsAspect {

    @Pointcut("execution(public * com.chromatic.modules.*.controller.*.*(..))")
    private void paramsFilter() {
    }

    @Pointcut("execution(public * com.chromatic.modules.*.service.impl.*.update(..))")
    private void parametersUpdate() {
    }

    @Around(value = "paramsFilter()")
    public <T> Object paramMapFilter(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();

        if (null != args && args.length > 0) {
            Object arg1 = args[0];
            if (arg1 instanceof Map) {
                ((Map<?, ?>) arg1).remove("_t");
                ((Map<?, ?>) arg1).remove("page");
                ((Map<?, ?>) arg1).remove("pageSize");
            }
        }
        return point.proceed(args);
    }

    @Around(value = "parametersUpdate()")
    public <T> Object handlerParametersOfUpdate(ProceedingJoinPoint point) throws Throwable {
        SysUserEntity user = ((SysUserEntity) SecurityUtils.getSubject().getPrincipal());
        Object[] args = point.getArgs();
        Object arg = args[0];
        if (arg != null) {
            if (CommonUtils.hasField(arg, "updateTime")) {
                CommonUtils.setter(arg, "updateTime", new Date());
            }
            if (CommonUtils.hasField(arg, "updateUser")) {
                CommonUtils.setter(arg, "updateUser", user.getName());
            }
        }
        Object proceed = point.proceed(args);

        return proceed;
    }

}
