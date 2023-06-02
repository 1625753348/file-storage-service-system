/**
 * Copyright (c) 2016-2019  All rights reserved.
 * <p>
 * https://www.7-me.net
 * <p>
 * 版权所有，侵权必究！
 */

package com.chromatic;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author Seven
 */
@EnableCaching
@MapperScans(value = {
        @MapperScan(value = "com.chromatic.modules.app.dao"),
        @MapperScan(value = "com.chromatic.modules.oss.dao"),
        @MapperScan(value = "com.chromatic.modules.sys.dao")
})
@SpringBootApplication
public class chromatic {

    public static void main(String[] args) {
        SpringApplication.run(chromatic.class, args);
    }

}