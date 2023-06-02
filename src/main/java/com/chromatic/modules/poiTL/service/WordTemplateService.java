package com.chromatic.modules.poiTL.service;

import com.chromatic.modules.poiTL.entity.DataModel;

import java.util.Map;


public interface WordTemplateService {

        /**
         * 组装渲染模板数据
         * @param dataModel
         * @param params
         * @return
         */
        DataModel assemblingDatagram(DataModel dataModel, Map<String, Object> params);

}
