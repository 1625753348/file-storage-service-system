package com.chromatic.common.utils;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

@Component
@Data
public class PoiTLUtil {
    private static Logger logger = LoggerFactory.getLogger(ConvertUtils.class);
    @Autowired
    private ApplicationContext applicationContext;
    public static PoiTLUtil poiTLUtil;

    @PostConstruct
    public void init() {
        poiTLUtil = this;

    }

    @javax.annotation.Resource
    ResourceLoader resourceLoader;


    /**
     * @param templateString 模板的路径
     * @param dataModel      模板要动态替换的数据  List<Map<String,Object>>
     * @param response       响应对象
     * @param outPutFileName 生成的WORD
     * @param config         自定义策略(合并DynamicTableRenderPolicy\动态行HackLoopTableRenderPolicy\代码块高亮HighlightRenderPolicy.. )
     * @throws IOException
     */
    public static void generateOutput(String templateString, Object dataModel, HttpServletResponse response, String outPutFileName, Configure config) throws IOException {
        InputStream resourceAsStream = PoiTLUtil.class.getClassLoader()
                .getResourceAsStream(templateString.replace("classpath:", ""));

        XWPFTemplate template = XWPFTemplate.compile(resourceAsStream, config).render(dataModel);
        template.getXWPFDocument().enforceUpdateFields(); // 更新目录

        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(outPutFileName, "UTF-8") + ".docx");

        ServletOutputStream servletOutputStream = null;
        try {
            servletOutputStream = response.getOutputStream();
            template.write(servletOutputStream);
        } finally {
            servletOutputStream.flush();
            servletOutputStream.close();
        }

    }

}
