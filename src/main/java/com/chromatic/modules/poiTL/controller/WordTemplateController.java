package com.chromatic.modules.poiTL.controller;
import com.chromatic.modules.poiTL.service.WordTemplateService;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;


import com.chromatic.common.exception.SevenmeException;
import com.chromatic.common.utils.PoiTLUtil;
import com.chromatic.common.vo.Result;


import com.chromatic.modules.poiTL.entity.DataModel;


import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(value = "/wordtemplate")
@Api(tags = "Word模板引擎")
public class WordTemplateController {

    @Autowired
    private static ResourceLoader resourceLoader;

    @Resource
    WordTemplateService wordTemplateService;


    @GetMapping("generateword")
    @ApiOperation("生成word")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jsaMainId", value = "分析单IO", paramType = "query", dataType = "String"),

    })
    public Result<ByteArrayOutputStream> generateword(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        Result<ByteArrayOutputStream> result = new Result<>();
        DataModel initDataModel = new DataModel();
        try {
            //组装数据包
            DataModel dataModel = wordTemplateService.assemblingDatagram(initDataModel, params);
            Map<String, Object> map = dataModel.getMap();
            Configure configure = dataModel.generateConfigure();
            PoiTLUtil.generateOutput("classpath:static/word/template.docx", map, response, "output",configure);
            return null;
        } catch (SevenmeException e) {
            e.printStackTrace();
            return result.error(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 自定义策略,设置动态行策略
     *
     * @return
     */
    public Configure getPolicy() {
        LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();

        Configure config = Configure.builder()
                .bind("cableList", policy)
                .bind("pipelineList", policy)
                .bind("equipmentList", policy)
                .bind("detailList", policy)
                .build();
        return config;
    }



}