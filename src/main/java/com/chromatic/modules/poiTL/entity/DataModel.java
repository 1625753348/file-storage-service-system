package com.chromatic.modules.poiTL.entity;

import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.policy.RenderPolicy;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data

public class DataModel {
    private Object data;
    private String label;

    private Map<String,Object> map ;

    private RenderPolicy policy;
    private ConfigureBuilder configureBuilder;
    private Configure configure;
    public DataModel(){
        this.configureBuilder = Configure.builder();
    };

    public DataModel( String label,Object data,RenderPolicy policy){
        this.configureBuilder = Configure.builder();
        this.data = data;
        this.label = label;
        this.map = new HashMap<String,Object>(){{put(label,data);}};
        this.policy = policy;
    }

    public  Configure generateConfigure(){
        Configure configure = this.configureBuilder.build();
        return configure;
    }

    //把所有要渲染的数据都加入数据模型中
    public DataModel assemble(DataModel dataModel){
        Map<String, Object> data1 = dataModel.getMap();
        if (this.map != null) {
            this.map.putAll(data1);
        }else {
            this.map = data1;
        }
        String label = dataModel.getLabel();
        RenderPolicy policy = dataModel.getPolicy();
        if (policy != null) {
            this.configureBuilder.bind(label, policy);
        }
        return this;
    }

    public DataModel assembleDataAndConfigureBuilders(List<DataModel> dataModels){
        for (DataModel dataModel : dataModels) {
            Map<String, Object> data1 = dataModel.getMap();
            if (this.map != null) {
                this.map.putAll(data1);
            }else {
                this.map = data1;
            }
            String label = dataModel.getLabel();
            RenderPolicy policy = dataModel.getPolicy();
            if (policy != null) {
                this.configureBuilder.bind(label, policy);
            }
        }

        return this;
    }



}
