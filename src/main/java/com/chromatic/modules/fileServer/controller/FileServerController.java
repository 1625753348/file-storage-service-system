package com.chromatic.modules.fileServer.controller;
import com.chromatic.modules.fileServer.service.impl.FileStrategyFactory;
import com.chromatic.modules.fileServer.service.impl.OSSFileStrategy;


import com.chromatic.common.exception.SevenmeException;
import com.chromatic.common.vo.Result;


import com.chromatic.modules.poiTL.entity.DataModel;


import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * @author Seven
 */
@RestController
@RequestMapping(value = "/fileserver")
@Api(tags = "文件服务器")
public class FileServerController {


    @Resource
    FileStrategyFactory fileStrategy;

    @GetMapping("generateword")
    @ApiOperation("生成word")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jsaMainId", value = "分析单IO", paramType = "query", dataType = "String"),

    })
    public Result<ByteArrayOutputStream> generateword(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        Result<ByteArrayOutputStream> result = new Result<>();
        DataModel initDataModel = new DataModel();
        try {
            //多态调用
            fileStrategy.getFileService("OSS").uplode();
            //调用独有的方法
            OSSFileStrategy oss = (OSSFileStrategy)fileStrategy.getFileService("OSS");
            oss.OSSuplode();
            return null;
        } catch (SevenmeException e) {
            e.printStackTrace();
            return result.error(e.getMessage());
        }
    }




}