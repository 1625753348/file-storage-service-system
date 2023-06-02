package com.chromatic.modules.poiTL.service.impl;

import com.chromatic.modules.poiTL.entity.DataModel;

import com.chromatic.modules.poiTL.service.WordTemplateService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WordTemplateServiceImpl implements WordTemplateService {
    private static final String RecordSeparator = Character.toString((char) 30);

    @Override
    public DataModel assemblingDatagram(DataModel dataModel,Map<String, Object> params) {

        return dataModel;
    }
}
