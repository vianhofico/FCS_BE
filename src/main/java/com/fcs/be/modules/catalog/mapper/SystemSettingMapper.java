package com.fcs.be.modules.catalog.mapper;

import com.fcs.be.modules.catalog.dto.response.SystemSettingResponse;
import com.fcs.be.modules.catalog.entity.SystemSetting;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SystemSettingMapper {

    SystemSettingResponse toSystemSettingResponse(SystemSetting systemSetting);
}