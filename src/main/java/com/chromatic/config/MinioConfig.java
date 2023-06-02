package com.chromatic.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/************************************************************************
 * @author: wg
 * @description:
 * @createTime: 11:25 2022/3/15
 * @updateTime: 11:25 2022/3/15
 ************************************************************************/
@Configuration
public class MinioConfig {

    @Resource
    MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }
}
