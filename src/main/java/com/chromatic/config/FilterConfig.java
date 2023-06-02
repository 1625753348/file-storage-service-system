/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.config;

import com.chromatic.common.xss.XssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;

/**
 * Filter配置
 * 配置 Filter 的主要目的是为了对请求进行拦截和处理，并保证每个请求都经过相应的 Filter 处理，从而实现自动化处理某些事情的需求。
 * @author Mark sunlightcs@gmail.com
 *
    setFilter - 设置要注册的 filter，例如 DelegatingFilterProxy("shiroFilter") 或 XssFilter 等。
    addInitParameter - 添加 init 参数，这是一些可以在过滤器初始化时传入的参数，例如 targetFilterLifecycle 参数等。
    setEnabled - 设置是否启用该过滤器，默认值为 true。
    setOrder - 设置该过滤器的执行顺序，数字越小，执行优先级越高。
    addUrlPatterns - 指定该过滤器要拦截的 URL 匹配规则。可以使用通配符"*"、"**"等来进行泛匹配。

    例如，通过 setEnabled(false) 可以暂时禁用某个过滤器；通过 setOrder(1) 可以将其设置成第一个要执行的过滤器，确保该过滤器能够最先处理请求；
    通过 addUrlPatterns("/api/*") 可以设置该过滤器只对 /api/ 开头的 URL 进行拦截和处理，从而达到精准控制目标 URL 的目的。其他更多的过滤器属性设定和操作方式，
    也可以根据具体情况进行参考和学习。`
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean shiroFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        //设置代理过滤器
        registration.setFilter(new DelegatingFilterProxy("shiroFilter"));
        //该值缺省为false，表示生命周期由SpringApplicationContext管理，设置为true则表示由ServletContainer管理
        //表示让 ServletContainer 来管理 filter 的生命周期
        registration.addInitParameter("targetFilterLifecycle", "true");

        //通过setEnabled、setOrder、addUrlPatterns 等方法设置过滤器的各种属性
        registration.setEnabled(true);
        registration.setOrder(Integer.MAX_VALUE - 1);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean xssFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }
}
