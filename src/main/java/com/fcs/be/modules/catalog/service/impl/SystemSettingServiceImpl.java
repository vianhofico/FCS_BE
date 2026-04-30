package com.fcs.be.modules.catalog.service.impl;

import com.fcs.be.modules.catalog.dto.request.UpdateSystemSettingRequest;
import com.fcs.be.modules.catalog.dto.response.SystemSettingResponse;
import com.fcs.be.modules.catalog.entity.SystemSetting;
import com.fcs.be.modules.catalog.mapper.SystemSettingMapper;
import com.fcs.be.modules.catalog.repository.SystemSettingRepository;
import com.fcs.be.modules.catalog.service.interfaces.SystemSettingService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemSettingServiceImpl implements SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;
    private final SystemSettingMapper systemSettingMapper;

    public SystemSettingServiceImpl(SystemSettingRepository systemSettingRepository, SystemSettingMapper systemSettingMapper) {
        this.systemSettingRepository = systemSettingRepository;
        this.systemSettingMapper = systemSettingMapper;
    }

    @Override
    public List<SystemSettingResponse> getSettings() {
        return systemSettingRepository.findByIsDeletedFalseOrderByCreatedAtDesc()
            .stream()
            .map(systemSettingMapper::toSystemSettingResponse)
            .toList();
    }

    @Override
    @Transactional
    public SystemSettingResponse updateSetting(UUID id, UpdateSystemSettingRequest request) {
        SystemSetting systemSetting = getSystemSettingEntity(id);
        systemSetting.setValue(request.value());
        systemSetting.setDescription(request.description());
        return systemSettingMapper.toSystemSettingResponse(systemSettingRepository.save(systemSetting));
    }

    private SystemSetting getSystemSettingEntity(UUID id) {
        return systemSettingRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("System setting not found"));
    }
}