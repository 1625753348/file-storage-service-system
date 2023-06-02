package com.chromatic.modules.fileServer.service.impl;


import org.springframework.web.multipart.MultipartFile;

/**
 * @author Seven
 */
public abstract class AbstractFileStrategy {
    private static final String databaseName = "jobSafetyAnalysis";

    public String assemblingObjectName(String tableName, String refId, String folderName, MultipartFile multipartFile){
        StringBuilder sb = new StringBuilder();
        sb.append(databaseName).append("/").append(tableName).append("/").append(refId).append("/");
        StringBuilder stringBuilder = folderName
                .equals("") ? sb.append(multipartFile.getOriginalFilename()) : sb.append(folderName).append("/").append(multipartFile.getOriginalFilename());

        return stringBuilder.toString();
    }
}

