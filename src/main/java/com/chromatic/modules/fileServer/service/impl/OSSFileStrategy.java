package com.chromatic.modules.fileServer.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import com.chromatic.common.exception.SevenmeException;
import com.chromatic.modules.fileServer.dto.DocumentFileDTO;
import com.chromatic.modules.fileServer.service.FileStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * @author Seven
 */
@Component("OSS")
public class OSSFileStrategy extends AbstractFileStrategy implements FileStrategy {

    private static final Logger logger = LoggerFactory.getLogger(OSSFileStrategy.class);

    private final String endpoint;

    private final OSS ossClient;

    private final String bucketName;

    @Autowired
    public OSSFileStrategy(OSS ossClient, @Value("${aliyun.oss.bucket-name}") String bucketName, @Value("${aliyun.oss.endpoint}") String endpoint) {
        this.ossClient = ossClient;
        this.bucketName = bucketName;
        this.endpoint = endpoint;
    }

    @Override
    public void createFolder(String folderName) {

        // 在bucket中创建目录，标记为0字节的Object
        String objectName = folderName.endsWith("/") ? folderName : folderName + "/";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(new byte[0]), metadata);

        // 释放资源
        ossClient.shutdown();
    }

    @Override
    public void deleteFolder(String objectName) {
        // 列举指定目录下所有的Object
        ListObjectsRequest request = new ListObjectsRequest(bucketName);
        request.setPrefix(objectName);
        ObjectListing objectListing = ossClient.listObjects(request);

        // 遍历所有Object
        while (true) {
            List<OSSObjectSummary> objects = objectListing.getObjectSummaries();
            for (OSSObjectSummary object : objects) {
                // 删除Object或目录
                ossClient.deleteObject(bucketName, object.getKey());
            }

            if (!objectListing.isTruncated()) {
                break;
            }

        }

        // 释放资源
        ossClient.shutdown();
    }

    @Override
    public Map<String, String> uploadFile(MultipartFile multipartFile, String folderName) {
        Map<String, String> map = new HashMap<String, String>();
        StringBuilder sb = new StringBuilder();
        String objectName = sb.append(folderName).append("/").append(multipartFile.getOriginalFilename()).toString();

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());
            PutObjectResult putObjectResult = ossClient.putObject(bucketName, objectName, inputStream);
        } catch (Exception e) {
            throw new SevenmeException("上传文件失败，请检查配置信息", e);
        }

        String  fileUrl = endpoint + "/" + objectName;
        map.put("fileUrl", fileUrl);
        return map;
    }

    @Override
    public List<DocumentFileDTO> lsDirectory(String directoryName) {
        List<Map<String, String>> arrayList = new ArrayList<>();
//        String bucketName = config.getAliyunBucketName();
//        try {
//            // 构造 ListObjectsRequest 请求对象，用于配置请求参数
//            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
//            listObjectsRequest.setPrefix(prefix);
//
//            // 调用 OSS 对象的 listObjects 方法返回一个包含所有对象的 ObjectListing 对象
//            ObjectListing objectListing = client.listObjects(listObjectsRequest);
//
//            // 处理当前批次获取到的文件
//            for (OSSObjectSummary object : objectListing.getObjectSummaries()) {
//                HashMap<String, String> map = new HashMap<>();
//                OSSObject ossObject = client.getObject(object.getBucketName(), object.getKey());
//
//                map.put("key", object.getKey());
//                map.put("sha256", getSha256(ossObject));
//                map.put("size", String.valueOf(object.getSize()));
//
//                GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, ossObject.getKey());
//                request.setExpiration(new Date(System.currentTimeMillis() + 24 * 3600 * 1000L));
//                String signature = client.generatePresignedUrl(request).getQuery();
//                arrayList.add(map);
//            }
//        } finally {
//            // 关闭 OSSClient 实例
//            // client.shutdown();
//        }
//
//        return arrayList;
        return null;
    }

    public void OSSuplode() {

    }

    @Override
    public void uplode() {

    }
}
