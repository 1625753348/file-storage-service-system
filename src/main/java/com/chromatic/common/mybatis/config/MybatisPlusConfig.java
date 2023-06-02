package com.chromatic.common.mybatis.config;

import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.chromatic.common.mybatis.interceptor.DataFilterInterceptor;
import com.chromatic.common.mybatis.interceptor.DictEchoInterceptor;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;

/**
 * mybatis-plus配置
 */
@Configuration
public class MybatisPlusConfig {

//    @Lazy
//    @Autowired
//    CommonDictService commonDictService;
    /**
     * 配置数据权限
     */
    @Bean
    @Order(1)
    public DataFilterInterceptor dataFilterInterceptor() {
        return new DataFilterInterceptor();
    }

    /**
     * 配置分页
     */
    @Bean
    @Order(0)
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    /**
     * 配置乐观锁
     */
    @Bean
    @Order(2)
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }

    /**
     * 配置字典翻译拦截器
     */
//    @Bean
//    @Order(3)
//    public DictEchoInterceptor dictEchoInterceptor() {
//        return new DictEchoInterceptor(commonDictService);
//    }

    /**
     * 配置 MybatisPlus 的全局配置
     */
//    @Bean
//    @Order(4)
//    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
//        return configuration -> {
//            // 添加字典翻译拦截器
//            configuration.addInterceptor(dictEchoInterceptor());
//        };
//    }

//    /**
//     * 配置 SqlSessionTemplate，设置全局拦截器
//     */
//    @Bean
//    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
//        sqlSessionFactory.getConfiguration().addInterceptor(dictEchoInterceptor());
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
}
