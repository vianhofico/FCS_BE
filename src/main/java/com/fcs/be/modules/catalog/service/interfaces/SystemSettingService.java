package com.fcs.be.modules.catalog.service.interfaces;

import com.fcs.be.modules.catalog.dto.request.UpdateSystemSettingRequest;
import com.fcs.be.modules.catalog.dto.response.SystemSettingResponse;
import java.util.List;
import java.util.UUID;

public interface SystemSettingService {

    List<SystemSettingResponse> getSettings();

    SystemSettingResponse updateSetting(UUID id, UpdateSystemSettingRequest request);
}