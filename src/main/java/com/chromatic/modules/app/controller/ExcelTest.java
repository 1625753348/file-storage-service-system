package com.chromatic.modules.app.controller;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.chromatic.modules.app.entity.IliDetailExcel;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.List;

/************************************************************************
 * @author: wg
 * @description:
 * @createTime: 10:21 2022/3/9
 * @updateTime: 10:21 2022/3/9
 ************************************************************************/
public class ExcelTest {

    public static void main(String[] args) {
        importTest();
    }

    public static void importTest() {
        String path = "static/excel/内检测数据.xlsx";
        ClassPathResource resource = new ClassPathResource(path);
        try {
            File file1 = resource.getFile();
            ImportParams importParams = new ImportParams();
            importParams.setTitleRows(0);
            importParams.setHeadRows(1);

            long currentTimeMillis = System.currentTimeMillis();

            List<IliDetailExcel> objectList = ExcelImportUtil.importExcel(file1, IliDetailExcel.class, importParams);

            objectList.forEach(item -> System.out.println(item));

            long l = System.currentTimeMillis();
            System.out.println("用时: " + (l - currentTimeMillis) + " 毫秒");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testDict() {

    }
}
