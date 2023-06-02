package com.chromatic.modules.fileServer.service;

import com.chromatic.modules.fileServer.dto.DocumentFileDTO;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author Seven
 */
public interface FileStrategy {

    public void uplode();

    /**
     * 创建文件夹
     * @param folderName = databaseName + "/" + tableName + "/" + refId + "/" +folderName;
     */
    void createFolder(String folderName);

    /**
     * 删除对象
     * @param objectName = databaseName + "/" + tableName + "/" + refId + "/" +objectName;
     */
    void deleteFolder(String objectName);

    /**
     * 上传文件
     * @param multipartFile
     * @param folderName = databaseName + "/" + tableName + "/" + refId + "/" +folderName;
     * @return
     */
    Map<String, String> uploadFile(MultipartFile multipartFile,String folderName);

    /**
     * 查看目录下的文件:
     * @param directoryName = databaseName + "/" + tableName + "/" + refId + "/" +folderName;
     * @return
     */
    List<DocumentFileDTO> lsDirectory (String directoryName);
//
//    上传文件夹:
//
//

//    预览:

//    下载:
//
//    大文件分片上传、合并:
//
//    按照{类型}{时间}{等}统计<所有>[文件](大小和数量):  statistics

}
