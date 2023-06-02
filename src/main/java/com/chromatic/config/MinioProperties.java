package com.chromatic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/************************************************************************
 * @author: wg
 * @description:
 * @createTime: 11:28 2022/3/15
 * @updateTime: 11:28 2022/3/15
 ************************************************************************/
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    private String bucketName;
    private String endpoint;
    private String accessKey;
    private String secretKey;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
