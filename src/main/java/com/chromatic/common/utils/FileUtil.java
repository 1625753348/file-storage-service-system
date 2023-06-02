package com.chromatic.common.utils;

import cn.hutool.core.io.resource.ClassPathResource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.util.FileUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/************************************************************************
 * @author: wg
 * @description:
 * @createTime: 15:06 2022/3/15
 * @updateTime: 15:06 2022/3/15
 ************************************************************************/
public class FileUtil {

    /**
     * 判断文件名是否带盘符，重新处理
     *
     * @param fileName
     * @return
     */
    public static String reviseFileName(String fileName) {
        //判断是否带有盘符信息
        // Check for Unix-style path
        int unixSep = fileName.lastIndexOf('/');
        // Check for Windows-style path
        int winSep = fileName.lastIndexOf('\\');
        // Cut off at latest possible point
        int pos = (winSep > unixSep ? winSep : unixSep);
        if (pos != -1) {
            // Any sort of path separator found...
            fileName = fileName.substring(pos + 1);
        }
        //替换上传文件名字的特殊字符
        fileName = fileName.replace("=", "").replace(",", "").replace("&", "")
                .replace("#", "").replace("“", "").replace("”", "");
        //替换上传文件名字中的空格
        fileName = fileName.replaceAll("\\s", "");
        return fileName;
    }

    /***************************************************
     * 获取 前端 上传的文件信息
     * @author: wg
     * @time: 2020/4/26 11:38
     ***************************************************/
    public static List<MultipartFile> getUploadFiles(HttpServletRequest request) {
        // (这里使用Vector，而不使用ArrayLsit，是怕引起线程安全问题，因为后面会引用到相同的内存地址)
        List<MultipartFile> fileVector = new Vector<>();

        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if (request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;

            Iterator<String> iterator = multipartHttpServletRequest.getFileNames();

            if (commonsMultipartResolver.isMultipart(request)) {
                while (iterator.hasNext()) {
                    // 将当前文件名一致的文件流放入同一个集合中
                    List<MultipartFile> fileRows = multipartHttpServletRequest.getFiles(iterator.next());

                    // 对文件做去重设置
                    // 判断集合是否存在，并且是否大于0
                    if (fileRows != null && fileRows.size() != 0) {
                        for (MultipartFile file : fileRows) {
                            String name = file.getName();

                            if (file != null && !file.isEmpty()) {
                                fileVector.add(file);
                            }
                        }
                    }
                }
                return fileVector;
            }
        }
        return fileVector;
    }

    /************************************************************************
     * @author: wg
     * @description: 把 map 写入 json 文件中
     * @params:
     * @return:
     * @createTime: 14:16  2022/3/14
     * @updateTime: 14:16  2022/3/14
     ************************************************************************/
    public static void writeToJson(Map map, ClassPathResource resource) throws IOException {
        File file = new File(resource.getAbsolutePath());

        // String path = "classpath:json/informationSchema.json";
        // File file = ResourceUtils.getFile(path);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(file, map);
    }

    /************************************************************************
     * @author: wg
     * @description: 读取 json 文件
     * @params:
     * @return:
     * @createTime: 14:12  2022/3/14
     * @updateTime: 14:12  2022/3/14
     ************************************************************************/
    public static JsonNode readJson(ClassPathResource resource) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = FileUtils.readFile(new FileInputStream(resource.getFile()));
        return mapper.readTree(jsonStr);
    }

    /************************************************************************
     * @author: wg
     * @description: 获取文件扩展名
     * @params:
     * @return:
     * @createTime: 10:42  2022/4/20
     * @updateTime: 10:42  2022/4/20
     ************************************************************************/
    public static String getExtensionName(MultipartFile multipartFile) {
        String fileName = reviseFileName(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * MultipartFile 转 File
     *
     * @param file
     * @throws Exception
     */
    public static File multipartFileToFile(MultipartFile file) throws Exception {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getHexHash(MultipartFile multipartFile) throws Exception {
        // 对 multipartfile 的内容 生成 hash
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(multipartFile.getBytes());
        byte[] digest1 = messageDigest.digest();
        return new BigInteger(1, digest1).toString(16);
    }

    public static String getHexHash(File file) throws Exception {
        // 对 file 的内容 生成 hash
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(cn.hutool.core.io.FileUtil.readBytes(file));
        byte[] digest1 = messageDigest.digest();
        return new BigInteger(1, digest1).toString(16);
    }
}
