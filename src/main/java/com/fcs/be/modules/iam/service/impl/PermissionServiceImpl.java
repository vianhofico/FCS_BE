package com.fcs.be.modules.iam.service.impl;

import com.fcs.be.modules.iam.dto.request.CreatePermissionRequest;
import com.fcs.be.modules.iam.dto.response.PermissionResponse;
import com.fcs.be.modules.iam.entity.Permission;
import com.fcs.be.modules.iam.mapper.PermissionMapper;
import com.fcs.be.modules.iam.repository.PermissionRepository;
import com.fcs.be.modules.iam.service.interfaces.PermissionService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    public PermissionServiceImpl(PermissionRepository permissionRepository, PermissionMapper permissionMapper) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<PermissionResponse> getPermissions() {
        return permissionRepository.findByIsDeletedFalse().stream()
            .map(permissionMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        Permission permission = new Permission();
        permission.setCode(request.code());
        permission.setName(request.name());
        permission.setDescription(request.description());
        permission.setModule(request.module());
        return permissionMapper.toResponse(permissionRepository.save(permission));
    }

    @Override
    @Transactional
    public PermissionResponse updatePermission(UUID id, CreatePermissionRequest request) {
        Permission permission = getPermissionEntity(id);
        permission.setCode(request.code());
        permission.setName(request.name());
        permission.setDescription(request.description());
        permission.setModule(request.module());
        return permissionMapper.toResponse(permissionRepository.save(permission));
    }

    @Override
    @Transactional
    public void deletePermission(UUID id) {
        Permission permission = getPermissionEntity(id);
        permission.setDeleted(true);
        permissionRepository.save(permission);
    }

    private Permission getPermissionEntity(UUID id) {
        return permissionRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Permission not found"));
    }
}
