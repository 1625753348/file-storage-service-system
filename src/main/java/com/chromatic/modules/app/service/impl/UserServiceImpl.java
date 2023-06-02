package com.chromatic.modules.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chromatic.modules.app.entity.UserEntity;
import com.chromatic.modules.app.form.LoginForm;
import com.chromatic.modules.app.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/************************************************************************
 * @author: wg
 * @description:
 * @createTime: 10:58 2022/8/15
 * @updateTime: 10:58 2022/8/15
 ************************************************************************/
@Service
public class UserServiceImpl implements UserService {

    @Override
    public UserEntity queryByMobile(String mobile) {
        return null;
    }

    @Override
    public long login(LoginForm form) {
        return 0;
    }

    @Override
    public boolean saveBatch(Collection<UserEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<UserEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<UserEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(UserEntity entity) {
        return false;
    }

    @Override
    public UserEntity getOne(Wrapper<UserEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<UserEntity> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<UserEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public BaseMapper<UserEntity> getBaseMapper() {
        return null;
    }
}
