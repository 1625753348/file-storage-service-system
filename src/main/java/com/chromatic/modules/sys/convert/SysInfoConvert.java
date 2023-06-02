package com.chromatic.modules.sys.convert;

import com.chromatic.modules.sys.dto.SysInfoDTO;
import com.chromatic.modules.sys.entity.SysUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SysInfoConvert {
    SysInfoConvert INSTANCE = Mappers.getMapper(SysInfoConvert.class);


    SysInfoDTO EntityToDTO(SysUserEntity entity);
}
