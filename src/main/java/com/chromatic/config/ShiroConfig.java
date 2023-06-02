/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic.config;

import com.chromatic.modules.sys.oauth2.OAuth2Filter;
import com.chromatic.modules.sys.oauth2.OAuth2Realm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro配置
 *
 * @author Mark sunlightcs@gmail.com
 */
@Configuration
public class ShiroConfig {

    /**
     * OAuth2Realm
     * doGetAuthorizationInfo() 函数返回了一个 SimpleAuthorizationInfo 实例。该实例中存储了当前用户的角色和权限信息
     * doGetAuthenticationInfo 定义认证方法,
     * @param oAuth2Realm
     * @return
     */
    @Bean("securityManager")
    public SecurityManager securityManager(OAuth2Realm oAuth2Realm) {
        //创建DefaultWebSecurityManager 对象
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置其使用的 Realm 为 自定义的Outh2Realm
        securityManager.setRealm(oAuth2Realm);
        //无需记住密码
        securityManager.setRememberMeManager(null);
        return securityManager;
    }

    /**
     * 其 key 表示需要进行权限验证的 URL 路径，value 则表示该 URL 对应的一条过滤器链。
     *
     * 通常情况下，filterChainDefinitionMap 的 value 中可以包含以下几个常见的元素：
     *
     *     anon：表示该 URL 可以被匿名访问，不需要进行任何身份认证；
     *     authc：表示需要进行身份认证才能够访问该 URL；
     *     roles：表示用户必须具有某些角色才能够访问该 URL；
     *     perms：表示用户必须具有某些权限才能够访问该 URL；
     *     port：表示仅当请求的端口号和配置的端口号相同时才能够访问该 URL；
     *     rest：表示该 URL 需要通过 RESTful 方式进行访问；
     *     ssl：表示该 URL 仅支持 HTTPS 访问。
     * @param securityManager
     * @return
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        //oauth过滤
        Map<String, Filter> filters = new HashMap<>();
            filters.put("oauth2", new OAuth2Filter());
        shiroFilter.setFilters(filters);

        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/webjars/**", "anon");
        filterMap.put("/druid/**", "anon");
        filterMap.put("/sys/login", "anon");

        filterMap.put("/sys/user", "anon");

        filterMap.put("/swagger/**", "anon");
        filterMap.put("/v2/api-docs", "anon");
        filterMap.put("/swagger-ui.html", "anon");
        filterMap.put("/swagger-resources/**", "anon");
        filterMap.put("/captcha.jpg", "anon");
        filterMap.put("/aaa.txt", "anon");

        filterMap.put("/app/miniotest/**", "anon");
        filterMap.put("/app/miniocontroller/**", "anon");
        filterMap.put("/jobsafetyanalysis/**", "anon");


        // <!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边
         filterMap.put("/**", "oauth2");

        shiroFilter.setFilterChainDefinitionMap(filterMap);

        return shiroFilter;
    }

    /**
     *
     * @return
     * 它是一个用于处理指定对象生命周期回调的后处理器。具体来说，它实现了对实现了 org.springframework.beans.factory.InitializingBean 和/或 org.springframework.beans.factory.DisposableBean 接口的 Bean 的特殊处理。
     * 在 Spring 应用程序的生命周期过程中，Bean 的初始化和销毁是两个关键事件，可以使用 LifecycleBeanPostProcessor 来实现在这些事件前后执行其他自定义逻辑
     */
    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启 Shiro 注解支持，例如 @RequiresPermissions、@RequiresRoles 等
     * AuthorizationAttributeSourceAdvisor 是一个 Spring AOP 的切面，用于对目标方法的调用进行拦截和授权处理。
     *
     * 要使用 AuthorizationAttributeSourceAdvisor，需要先定义好一组权限表达式并将其配置到 Spring Security 中。
     * 然后，在需要进行授权的方法或类上添加相应的注解（例如 @RequiresRoles、@RequiresPermissions 等）以指定访问限制规则。
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

}
