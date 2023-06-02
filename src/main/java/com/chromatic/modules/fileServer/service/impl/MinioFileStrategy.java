package com.chromatic.modules.fileServer.service.impl;

import com.chromatic.modules.fileServer.dto.DocumentFileDTO;
import com.chromatic.modules.fileServer.service.FileStrategy;

import io.minio.*;
import io.minio.messages.Item;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Date;
import java.util.*;

import static com.chromatic.common.utils.MyMinioUtil.getFileContentType;

/**
 * @author Seven
 */
@Component("Minio")
public class MinioFileStrategy extends AbstractFileStrategy implements FileStrategy {
    private static final Logger logger = LoggerFactory.getLogger(MinioFileStrategy.class);
    private final MinioClient minioClient;
    private final String bucketName;

    @Autowired
    public MinioFileStrategy(MinioClient minioClient, @Value("${minio.bucketName}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }
    @Override
    public void uplode() {

    }

    @Override
    public void createFolder(String folderName) {
        try {
            // 检查存储桶是否已经存在
            if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                logger.info("Bucket already exists.");
            } else {
                // 创建一个名为ota的存储桶
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                logger.info("create a new bucket.");
            }

            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(bucketName)
                    .object(folderName)
                    .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                    .build();
            minioClient.putObject(objectArgs);


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void deleteFolder(String objectName) {
        try {
        if (StringUtils.isNotBlank(objectName)) {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(objectName)
                            .recursive(false)
                            .build()
            );
            for (Result<Item> result : results) {
                Item item = result.get();
                if (item.isDir()) {
                    deleteFolder(item.objectName());

                    continue;
                }
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(item.objectName())
                                .build()
                );
            }
            logger.info("删除文件: {}", objectName);
        }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public Map<String, String> uploadFile(MultipartFile multipartFile, String folderName) {
        Map<String, String> map = new HashMap<String, String>();
        StringBuilder sb = new StringBuilder();
        String objectName = sb.append(folderName).append("/").append(multipartFile.getOriginalFilename()).toString();

        try {
            // 检查存储桶是否已经存在
            if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                logger.info("Bucket already exists.");
            } else {
                // 创建一个名为ota的存储桶
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                logger.info("create a new bucket.");
            }

            InputStream stream = multipartFile.getInputStream();
            String orgName = multipartFile.getOriginalFilename();
//获取文件名
//或会出现异常
//            String orgName = multipartFile.getOriginalFilename();
//            if ("".equals(orgName)) {
//                orgName = multipartFile.getName();
//            }
//            orgName = FileUtil.reviseFileName(orgName);
//
//            String objectName = orgName;
//            // 使用putObject上传一个本地文件到存储桶中。
//            if (objectName.startsWith("/")) {
//                objectName = objectName.substring(1);
//            }

            String fileUrl = "/" + bucketName + "/" + objectName;
            map.put("fileUrl", fileUrl);

            PutObjectArgs objectArgs = PutObjectArgs.builder()
                    .object(objectName)
                    .bucket(bucketName)
                    .contentType(getFileContentType(orgName))
                    .stream(stream, stream.available(), -1)
                    .build();
            minioClient.putObject(objectArgs);

            stream.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return map;
    }

    @Override
    public List<DocumentFileDTO> lsDirectory(String directoryName) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(directoryName)
                        .recursive(false)
                        .build());
        List<DocumentFileDTO> docDTOList = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                Item item = result.get();
                DocumentFileDTO docDTO = new DocumentFileDTO();
                if (item.objectName().equals(directoryName)) {
                    continue;
                }

                String[] paths = item.objectName().split("/");
                docDTO.setFilePath(item.objectName());
                docDTO.setFileName(paths[paths.length - 1]);
                docDTO.setRefId(Long.parseLong(paths[2]));
                docDTO.setTableName(paths[1]);
                docDTO.setUpdateTime(Date.from(item.lastModified().toInstant()));
                docDTO.setLastedModified(Date.from(item.lastModified().toInstant()));

                docDTOList.add(docDTO);

                if (item.isDir()||!paths[paths.length-1].contains(".")) {
                    docDTO.setIsDir(true);

                    //objectItem.setDocPath(Fpath);

                    continue;
                }

                String dd = paths[paths.length - 1];
                String[] pa = dd.split("\\.");


                if (!Objects.isNull(pa) && pa.length > 1) {
                    docDTO.setFileExt(pa[1]);
                }

                docDTO.setIsDir(item.isDir());
                docDTO.setFileSize(item.size());
                //OSSObject ossObject = minioClient.getObject(bucketName, item.objectName());

                docDTOList.add(docDTO);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return docDTOList;
    }

}
