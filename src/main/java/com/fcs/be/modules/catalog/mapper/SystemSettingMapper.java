package com.fcs.be.modules.catalog.mapper;

import com.fcs.be.modules.catalog.dto.response.SystemSettingResponse;
import com.fcs.be.modules.catalog.entity.SystemSetting;
import org.springframework.stereotype.Component;

@Component
public class SystemSettingMapper {

    public SystemSettingResponse toSystemSettingResponse(SystemSetting systemSetting) {
        return new SystemSettingResponse(
            systemSetting.getId(),
            systemSetting.getKey(),
            systemSetting.getValue(),
            systemSetting.getDescription()
        );
    }
}