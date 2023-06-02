package com.chromatic.modules.fileServer.service.impl;

import com.chromatic.modules.fileServer.service.FileStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Seven
 */
@Component
public class FileStrategyFactory {

    @Autowired
    Map<String, FileStrategy> strategys = new ConcurrentHashMap<String, FileStrategy>(5);

    /**
     * 静态方法方便非SpringBean对象方法中调用
     * @param
     * @return
     */
    public  FileStrategy getFileService(@NotNull String component){
        FileStrategy strategy = strategys.get(component);
        if(strategy == null) {
            throw new RuntimeException("no strategy defined");
        }
        return strategy;
    }


}
