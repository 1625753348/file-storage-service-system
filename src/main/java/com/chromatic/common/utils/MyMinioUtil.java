package com.chromatic.common.utils;

import cn.hutool.core.util.StrUtil;
import com.chromatic.config.MinioProperties;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.TimeUnit;

/************************************************************************
 * @author: wg
 * @description:
 * @createTime: 15:57 2022/3/15
 * @updateTime: 15:57 2022/3/15
 ************************************************************************/
@Component
public class MyMinioUtil {
    private static final Logger logger = LoggerFactory.getLogger(MyMinioUtil.class);

    private static MyMinioUtil myMinioUtil;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String minioUrl;

    @Resource
    private MinioProperties minioProperties;

    @Resource
    MinioClient minioClient;

//    @Resource
//    private DocumentFileDao documentFileDao;

    @PostConstruct
    private void init() {
        myMinioUtil = this;
    }

    /**
     * 上传文件
     */
    public static Map<String, String> uploadReport(MultipartFile multipartFile, String tableName, String refId, String folderName) {
        Map<String, String> map = new HashMap<String, String>();
        String fileUrl = "";
        String newBucket = myMinioUtil.bucketName;
        try {
            // 检查存储桶是否已经存在
            if (myMinioUtil.minioClient.bucketExists(BucketExistsArgs.builder().bucket(newBucket).build())) {
                logger.info("Bucket already exists.");
            } else {
                // 创建一个名为ota的存储桶
                myMinioUtil.minioClient.makeBucket(MakeBucketArgs.builder().bucket(newBucket).build());
                logger.info("create a new bucket.");
            }
            // 过滤上传文件类型
            //FileTypeFilter.fileTypeFilter(multipartFile);
            InputStream stream = multipartFile.getInputStream();
            // 获取文件名
            String orgName = multipartFile.getOriginalFilename();
            if ("".equals(orgName)) {
                orgName = multipartFile.getName();
            }
            orgName = FileUtil.reviseFileName(orgName);

            String objectName = orgName;
            // 使用putObject上传一个本地文件到存储桶中。
            if (objectName.startsWith("/")) {
                objectName = objectName.substring(1);
            }

            // objectName = tableName + "/" + hexHash + "/" + orgName;
            objectName = tableName + "/" + refId + "/" + folderName + "/" + orgName;
            fileUrl = "/" + newBucket + "/" + objectName;
            String tempUrl = fileUrl;
            map.put("fileUrl", fileUrl);
            PutObjectArgs objectArgs = PutObjectArgs.builder()
                    .object(objectName)
                    .bucket(newBucket)
                    .contentType(getFileContentType(orgName))
                    .stream(stream, stream.available(), -1)
                    .build();
            myMinioUtil.minioClient.putObject(objectArgs);

            stream.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return map;
    }


    /**
     * 新建文件夹
     */
    public static String uploadFolderReport(String folderName, String tableName, String refId) {

        String fileUrl = "";
        String newBucket = myMinioUtil.bucketName;
        String objectName = null;
        try {
            // 检查存储桶是否已经存在
            if (myMinioUtil.minioClient.bucketExists(BucketExistsArgs.builder().bucket(newBucket).build())) {
                logger.info("Bucket already exists.");
            } else {
                // 创建一个名为ota的存储桶
                myMinioUtil.minioClient.makeBucket(MakeBucketArgs.builder().bucket(newBucket).build());
                logger.info("create a new bucket.");
            }


            // objectName = tableName + "/" + hexHash + "/" + orgName;
            objectName = tableName + "/" + refId + "/" + folderName + "/";
            //objectName = "temp" + "/" + tableName + "/" + hexHash + "/" + orgName;
            fileUrl = "/" + newBucket + "/" + objectName;


            PutObjectArgs objectArgs = PutObjectArgs.builder()
                    .object(objectName)
                    .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                    .bucket(newBucket)
                    .build();
            myMinioUtil.minioClient.putObject(objectArgs);


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return objectName;
    }


    /************************************************************************
     * @author: wg
     * @description: 预览地址
     * @params:
     * @return:
     * @createTime: 18:30  2022/4/7
     * @updateTime: 18:30  2022/4/7
     ************************************************************************/
    public static String getPreviewURL(@Nullable String bucketName, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        objectName = objectName.trim();
        if (StringUtils.isBlank(bucketName)) {
            bucketName = myMinioUtil.bucketName;
        }
        Map<String, String> reqParams = new HashMap<String, String>();
        reqParams.put("response-content-type", getFileContentType(objectName));
        GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(objectName)
                .extraQueryParams(reqParams)
                .build();
        return myMinioUtil.minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
    }

    public static String getPreviewURL(@Nullable String bucketName, MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 获取文件名
        String orgName = file.getOriginalFilename();
        if ("".equals(orgName)) {
            orgName = file.getName();
        }
        orgName = FileUtil.reviseFileName(orgName);

        String objectName = orgName;

        if (StringUtils.isBlank(bucketName)) {
            bucketName = myMinioUtil.bucketName;
        }
        // int expiry = Integer.MAX_VALUE;

        Map<String, String> reqParams = new HashMap<String, String>();
        reqParams.put("response-content-type", getFileContentType(objectName));
        return myMinioUtil.minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(1, TimeUnit.SECONDS)
                        .extraQueryParams(reqParams)
                        .build()
        );
    }

    /**
     * 根据文件后缀, 确定 PutObjectArgs.builder().contentType
     */
    public static String getFileContentType(String fileName) {
        if (fileName.contains(".")) {
            String returnFileName = fileName.substring(fileName.lastIndexOf("."));
            switch (returnFileName) {
                case ".jpeg":
                case ".png":
                case ".jpg":
                case ".webp":
                    return "image/jpeg";
                case ".mp4":
                    return "video/mp4";
                case ".html":
                    return "text/html";
                case ".css":
                    return "text/css";
                case ".js":
                    return "application/javascript";
                case ".pdf":
                    return "application/pdf";
                default:
                    return "application/octet-stream";
            }
        } else {
            return "application/octet-stream";
        }
    }

    /**
     * 下载文件
     *
     * @param originalName 文件路径
     */
    public InputStream downloadFile(String originalName, HttpServletResponse response) {
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(myMinioUtil.bucketName)
                    .object(originalName)
                    .build();
            InputStream file = myMinioUtil.minioClient.getObject(getObjectArgs);
            String filename = new String(originalName.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
            if (StrUtil.isNotBlank(originalName)) {
                filename = originalName;
            }
            response.setCharacterEncoding("utf-8");
            //设置强制下载不打开
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            ServletOutputStream servletOutputStream = response.getOutputStream();
            int len;
            byte[] buffer = new byte[1024];
            while ((len = file.read(buffer)) != -1) {
                servletOutputStream.write(buffer, 0, len);
            }
            servletOutputStream.flush();
            file.close();
            servletOutputStream.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 文件下载
     */
    public static void download(String objectName, String fileName, HttpServletResponse res) {
        GetObjectArgs objectArgs = GetObjectArgs.builder()
                .bucket(myMinioUtil.minioProperties.getBucketName())
                .object(objectName)
                .build();
        try (GetObjectResponse response = myMinioUtil.minioClient.getObject(objectArgs)) {
            byte[] buf = new byte[1024];
            int len;
            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
                while ((len = response.read(buf)) != -1) {
                    os.write(buf, 0, len);
                }
                os.flush();
                byte[] bytes = os.toByteArray();
                res.setCharacterEncoding("utf-8");
                //设置强制下载不打开
                res.setContentType("application/force-download");
                res.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
                try (ServletOutputStream stream = res.getOutputStream()) {
                    stream.write(bytes);
                    stream.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /************************************************************************
     * @author: wg
     * @description: 删除文件
     * @params:
     * @return:
     * @createTime: 14:55  2022/4/21
     * @updateTime: 14:55  2022/4/21
     ************************************************************************/
    public static void deleteObject(String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (StringUtils.isNotBlank(objectName)) {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(myMinioUtil.bucketName)
                    .object(objectName)
                    .build();
            myMinioUtil.minioClient.removeObject(removeObjectArgs);
            logger.info("删除文件: {}", objectName);
        }
    }


    //递归删除文件夹及下文件
    public static void deleteObjectgroup(String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (StringUtils.isNotBlank(objectName)) {

            Iterable<Result<Item>> results = myMinioUtil.minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(myMinioUtil.bucketName)
                            .prefix(objectName)
                            .recursive(false)
                            .build()
            );
            for (Result<Item> result : results) {
                Item item = result.get();
                if (item.isDir()) {
                    deleteObjectgroup(item.objectName());

                    continue;
                }
                myMinioUtil.minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(myMinioUtil.bucketName)
                                .object(item.objectName())
                                .build()
                );
            }
            logger.info("删除文件: {}", objectName);
        }
    }


    public static boolean isInMinioExist(MultipartFile multipartFile, String tableName) {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileName = tableName + "/" + originalFilename;
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(myMinioUtil.bucketName)
                    .object(fileName)
                    .build();
            GetObjectResponse response = myMinioUtil.minioClient.getObject(getObjectArgs);

            String object = response.object();
            String objectName = object.substring(object.lastIndexOf("/"));
            String tName = object.substring(0, object.lastIndexOf("/"));

            // ↓↓******************* <解析文件的散列值> start *******************↓↓
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(multipartFile.getBytes());
            byte[] digest = messageDigest.digest();
            String hexHash = new BigInteger(1, digest).toString(16);
            // ↑↑******************* <解析文件的散列值> end   *******************↑↑

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /************************************************************************
     * @author: wg
     * @description: 保存到持久路径
     * @params:
     * @return:
     * @createTime: 17:34  2022/4/26
     * @updateTime: 17:34  2022/4/26
     ************************************************************************/
//    public static DocumentFileEntity saveFile(DocumentFileEntity documentFileEntity) {
//        String tempObjectName = documentFileEntity.getFilePath().split("/sims-sl")[1];
//        String bucketName = myMinioUtil.bucketName;
//        String realObjectName = "";
//        String realFilePath = "";
//        Long refId = documentFileEntity.getRefId();
//        String tableName = documentFileEntity.getTableName();
//        String hexHash = documentFileEntity.getHexHash();
//        String fileName = documentFileEntity.getFileName();
//
//        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
//                .bucket(myMinioUtil.minioProperties.getBucketName())
//                .object(tempObjectName)
//                .build();
//
//        try (GetObjectResponse response = myMinioUtil.minioClient.getObject(getObjectArgs)) {
//
//            byte[] buf = new byte[1024];
//            int len;
//            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
//                while ((len = response.read(buf)) != -1) {
//                    os.write(buf, 0, len);
//                }
//                os.flush();
//                byte[] bytes = os.toByteArray();
//
//                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
//
//                realObjectName = "/" + tableName + "/" + refId + "/" + hexHash + "/" + fileName;
//                realFilePath = "/sims-sl" + realObjectName;
//                documentFileEntity.setFilePath(realFilePath);
//
//                PutObjectArgs objectArgs = PutObjectArgs.builder()
//                        .object(realObjectName)
//                        .bucket(bucketName)
//                        .contentType(getFileContentType(realObjectName))
//                        .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
//                        .build();
//                myMinioUtil.minioClient.putObject(objectArgs);
//
//                byteArrayInputStream.close();
//                os.close();
//                response.close();
//            }
//            deleteObject(tempObjectName);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return documentFileEntity;
//    }

    /************************************************************************
     * @author: wg
     * @description: 通球图片上传
     * @params:
     * @return:
     * @createTime: 16:14  2022/4/27
     * @updateTime: 16:14  2022/4/27
     ************************************************************************/
//    public static MaintainPiggingPictureEntity savePiggingPicture(MaintainPiggingPictureEntity pictureEntity) {
//        String tempObjectName = pictureEntity.getImgPath().split("/sims-sl")[1];
//        String bucketName = myMinioUtil.bucketName;
//        String realObjectName = "";
//        String realFilePath = "";
//        Long piggingHistoryId = pictureEntity.getMaintainPiggingHistoryId();
//        String tableName = "maintain_pigging_history";
//        String hexHash = pictureEntity.getHexHash();
//        String fileName = pictureEntity.getPictureName();
//
//        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
//                .bucket(myMinioUtil.minioProperties.getBucketName())
//                .object(tempObjectName)
//                .build();
//
//        try (GetObjectResponse response = myMinioUtil.minioClient.getObject(getObjectArgs)) {
//
//            byte[] buf = new byte[1024];
//            int len;
//            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
//                while ((len = response.read(buf)) != -1) {
//                    os.write(buf, 0, len);
//                }
//                os.flush();
//                byte[] bytes = os.toByteArray();
//
//                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
//
//                realObjectName = "/" + tableName + "/" + piggingHistoryId + "/" + hexHash + "/" + fileName;
//                realFilePath = "/sims-sl" + realObjectName;
//                pictureEntity.setImgPath(realFilePath);
//
//                PutObjectArgs objectArgs = PutObjectArgs.builder()
//                        .object(realObjectName)
//                        .bucket(bucketName)
//                        .contentType(getFileContentType(realObjectName))
//                        .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
//                        .build();
//                myMinioUtil.minioClient.putObject(objectArgs);
//
//                byteArrayInputStream.close();
//                os.close();
//                response.close();
//            }
//            deleteObject(tempObjectName);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return pictureEntity;
//    }

//    public static RepairDocFileDTO saveSpRepairDoc(RepairDocFileDTO repairDocFileDTO) {
//        String tempObjectName = "";
//        if (StringUtils.isNotEmpty(repairDocFileDTO.getFilePath())) {
//            tempObjectName = repairDocFileDTO.getFilePath().split("/sims-sl")[1];
//        }
//        String bucketName = myMinioUtil.bucketName;
//        String realObjectName = "";
//        String realFilePath = "";
//        Long refId = repairDocFileDTO.getRefId();
//        String tableName = repairDocFileDTO.getTableName();
//        String hexHash = repairDocFileDTO.getHexHash();
//        String fileName = repairDocFileDTO.getFileName();
//
//        GetObjectArgs getObjectArgs = null;
//        if (StringUtils.isNotEmpty(tempObjectName)) {
//            getObjectArgs = GetObjectArgs.builder()
//                    .bucket(myMinioUtil.minioProperties.getBucketName())
//                    .object(tempObjectName)
//                    .build();
//        }
//
//        if (getObjectArgs != null) {
//            try (GetObjectResponse response = myMinioUtil.minioClient.getObject(getObjectArgs)) {
//
//                byte[] buf = new byte[1024];
//                int len;
//                try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
//                    while ((len = response.read(buf)) != -1) {
//                        os.write(buf, 0, len);
//                    }
//                    os.flush();
//                    byte[] bytes = os.toByteArray();
//
//                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
//
//                    realObjectName = "/" + tableName + "/" + refId + "/" + hexHash + "/" + fileName;
//                    realFilePath = "/sims-sl" + realObjectName;
//                    repairDocFileDTO.setFilePath(realFilePath);
//
//                    PutObjectArgs objectArgs = PutObjectArgs.builder()
//                            .object(realObjectName)
//                            .bucket(bucketName)
//                            .contentType(getFileContentType(realObjectName))
//                            .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
//                            .build();
//                    myMinioUtil.minioClient.putObject(objectArgs);
//
//                    byteArrayInputStream.close();
//                    os.close();
//                    response.close();
//                }
//                deleteObject(tempObjectName);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return repairDocFileDTO;
//    }

//

    /**
     * @param originalName
     */
    public static StatObjectResponse getObjInfo(String originalName) {
        StatObjectResponse stat = null;
        try {
            stat = myMinioUtil.minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(myMinioUtil.bucketName)
                            .object(originalName)
                            .build());
            return stat;

        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        }
        // System.out.println(stat.toString());

    }

    /**
     * info_about_files_in_buckets
     * 找文件目录下的文件
     */
    @NotNull
    public static Map<String, String> info_about_files_in_buckets(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<Item>> myObjects = myMinioUtil.minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());

        Map<String, String> reqParams = new HashMap<String, String>();


        for (Result<Item> result : myObjects) {
            Item item = result.get();
            String s = item.objectName();
            System.out.println("item.objectName :  " + s);

            boolean latest = item.isLatest();
            if (item.isDir()) {
                continue;
            } else {

            }

//        System.out.println(item.lastModified() + ", " + item.size() + ", " + item.objectName());
            // reqParams.put("fileName", responses);
            //  reqParams.put("fileExt", responses);
            //reqParams.put("fileSize", responses);
        }
        return reqParams;
    }


    /**
     * 查看文件对象,文件夹中的
     *
     * @param path 文件夹 结尾必须有 /
     * @return 存储bucket内文件对象信息
     */
//    public static List<DocDTO> listObjects(String path, String tableName, String refId) {
//        //Boolean onlyDir = false;
//        String Fpath = path + "/";
//        Iterable<Result<Item>> results = myMinioUtil.minioClient.listObjects(
//                ListObjectsArgs.builder()
//                        .bucket(myMinioUtil.bucketName)
//                        .prefix(Fpath)
//                        .recursive(false)
//                        .build());
//        List<DocDTO> objectItems = new ArrayList<>();
//        try {
//            for (Result<Item> result : results) {
//                Item item = result.get();
//                DocDTO objectItem = new DocDTO();
//                if (item.isDir()) {
//
//                    objectItem.setIsDir(item.isDir());
//                    String[] paths = item.objectName().split("/");
//
//                    objectItem.setDocPath(item.objectName());
//                    objectItem.setFileName(paths[paths.length - 1]);
//                    //objectItem.setDocPath(Fpath);
//                    objectItem.setRefId(Long.valueOf(refId));
//                    objectItem.setTableName(tableName);
//
//                    objectItems.add(objectItem);
//
//                    continue;
//                }
//
//
//                String[] paths = item.objectName().split("/");
//
//                objectItem.setDocPath(Fpath + paths[paths.length - 1]);
//                objectItem.setFileName(paths[paths.length - 1]);
//                objectItem.setUpdateTime(Date.from(item.lastModified().toInstant()));
//                String dd = paths[paths.length - 1];
//                String[] pa = dd.split("\\.");
//
//                if (!Objects.isNull(pa) && pa.length > 1) {
//                    objectItem.setFileExt(pa[1]);
//                }
//
//                objectItem.setIsDir(item.isDir());
//                StatObjectResponse stat = getObjInfo(item.objectName());
//                objectItem.setFileSize(stat.size());
//                objectItem.setTableName(tableName);
//                objectItem.setRefId(Long.valueOf(refId));
//                objectItems.add(objectItem);
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        return objectItems;
//    }
    /**
     * 查看文件对象,文件夹中的
     *
     * @param path 文件夹 结尾必须有 /
     * @return 存储bucket所有文件对象refid信息
     */
//    public static List<DocumentStatisticsEntity> findAllLeafFilesRefId(String objectName ,List<DocumentStatisticsEntity> transfer) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//        Iterable<Result<Item>> results = myMinioUtil.minioClient.listObjects(
//                    ListObjectsArgs.builder()
//                            .bucket(myMinioUtil.bucketName)
//                            .prefix(objectName)
//                            .recursive(false)
//                            .build()
//            );
//
//            for (Result<Item> result : results) {
//                Item item = result.get();
//                if (item.isDir()) {
//                    findAllLeafFilesRefId(item.objectName(),transfer);
//                    continue;
//                }
//                DocumentStatisticsEntity entity = new DocumentStatisticsEntity();
//                entity.setDocPath(item.objectName());
//                String[] paths = item.objectName().split("/");
//                entity.setRefId(Long.valueOf(paths[1]));
//                transfer.add(entity);
//
//            }
//        return transfer;
//    }

}
