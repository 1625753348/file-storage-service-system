package com.chromatic.modules.fileServer.mapping;

import com.aliyun.oss.model.OSSObjectSummary;
import com.chromatic.modules.fileServer.dto.DocumentFileDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 *
 */
@Mapper(componentModel = "spring")
public abstract class FileMapping {


    @Mappings({
            @Mapping( source =  "tableName",target ="tableName"),
            @Mapping( source = "refId",target = "refId"),
            @Mapping( source = "fileName",target = "fileName"),
            @Mapping( source = "ossObjectSummary.key",target = "filePath"),
            @Mapping( source = "fileDesc",target = "fileDesc"),
            @Mapping( source = "fileExt",target = "fileExt"),
            @Mapping( source = "hexHash",target = "hexHash"),
            @Mapping( source = "ossObjectSummary.size",target = "fileSize"),
            @Mapping( source = "ossObjectSummary.lastModified",target = "lastedModified"),
            @Mapping( source = "previewUrl",target = "previewUrl"),
            @Mapping( source = "isDir",target = "isDir"),

    })
    public abstract DocumentFileDTO ossToDTO(OSSObjectSummary ossObjectSummary, String tableName,Long refId,
                                          String fileName,String fileDesc,String fileExt,String hexHash,String previewUrl,Boolean isDir);


//    @Mappings({
//            @Mapping( source =  "tableName",target ="tableName"),
//            @Mapping( source = "refId",target = "refId"),
//            @Mapping( source = "fileName",target = "fileName"),
//            @Mapping( source = "item.objectName",target = "filePath"),
//            @Mapping( source = "fileDesc",target = "fileDesc"),
//            @Mapping( source = "fileExt",target = "fileExt"),
//            @Mapping( source = "hexHash",target = "hexHash"),
//            @Mapping( source = "item.size",target = "fileSize"),
//            @Mapping( source = "item.lastModified",target = "lastedModified"),
//            @Mapping( source = "previewUrl",target = "previewUrl"),
//            @Mapping( source = "item.isDir",target = "isDir"),
//
//    })
//    public abstract DocumentFileDTO minioToDTO(Item item, String tableName, Long refId,
//                                               String fileName, String fileDesc, String fileExt, String hexHash, String previewUrl);


}