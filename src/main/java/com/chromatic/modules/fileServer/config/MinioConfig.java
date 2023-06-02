package com.chromatic.modules.fileServer.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Seven
 * //可以用配置文件@ConfigurationProperties(prefix = "minio")
 */
//@Configuration
public class MinioConfig {
    // @Resource
    // MinioProperties minioProperties;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {return MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
    }
}
