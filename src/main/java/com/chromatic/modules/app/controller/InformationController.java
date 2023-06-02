package com.chromatic.modules.app.controller;

import cn.hutool.core.io.resource.ClassPathResource;
import com.chromatic.modules.app.entity.KeyColumnUsage;
import com.chromatic.modules.app.service.InformationSchemaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/************************************************************************
 * @author: wg
 * @description:
 * @createTime: 10:54 2022/3/14
 * @updateTime: 10:54 2022/3/14
 ************************************************************************/
@RestController
@RequestMapping(value = "/app/integritymanagement/informationcontroller")
@Api(tags = "information")
public class InformationController {

    @Resource
    private InformationSchemaService informationSchemaService;

    @GetMapping(value = "/getallschema")
    @ApiOperation(value = "获取数据库表的所有外键关联, 然后写入json文件中")
    public void getAllSchema() {
        Map<String, List<KeyColumnUsage>> allSchema = informationSchemaService.getAllSchemaMap();
        allSchema.forEach((k, v) -> System.out.println(k + ": " + v));
        ClassPathResource resource = new ClassPathResource("/json/informationSchema.json");
        informationSchemaService.writeToJson(allSchema, resource);
    }

    @GetMapping(value = "/read")
    @ApiOperation(value = "读取json文件")
    public List<KeyColumnUsage> read() {
        String tableName = "common_facility";
        ClassPathResource classPathResource = new ClassPathResource("/json/informationSchema.json");
        List<KeyColumnUsage> keyColumnUsage = informationSchemaService.readJsonOfInformationSchema(tableName, classPathResource);
        return keyColumnUsage;
    }

}
