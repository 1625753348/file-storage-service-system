package com.chromatic.modules.sys.service.impl;

import com.chromatic.modules.sys.service.CacheService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = "verify")
@Service
public class CacheServiceImpl implements CacheService {

    @Override
    @Cacheable(key = "#codeKey")
    public String cacheVerifiedCode(String codeKey, String code) {
        return code;
    }
}
