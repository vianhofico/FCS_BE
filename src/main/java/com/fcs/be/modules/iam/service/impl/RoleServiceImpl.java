package com.fcs.be.modules.iam.service.impl;

import com.fcs.be.modules.iam.dto.request.AssignPermissionsRequest;
import com.fcs.be.modules.iam.dto.request.CreateRoleRequest;
import com.fcs.be.modules.iam.dto.response.RoleResponse;
import com.fcs.be.modules.iam.entity.Permission;
import com.fcs.be.modules.iam.entity.Role;
import com.fcs.be.modules.iam.entity.RolePermission;
import com.fcs.be.modules.iam.mapper.RoleMapper;
import com.fcs.be.modules.iam.repository.PermissionRepository;
import com.fcs.be.modules.iam.repository.RolePermissionRepository;
import com.fcs.be.modules.iam.repository.RoleRepository;
import com.fcs.be.modules.iam.service.interfaces.RoleService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(
        RoleRepository roleRepository,
        RolePermissionRepository rolePermissionRepository,
        PermissionRepository permissionRepository,
        RoleMapper roleMapper
    ) {
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public List<RoleResponse> getRoles() {
        return roleRepository.findByIsDeletedFalse().stream()
            .map(roleMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        if (roleRepository.findByNameAndIsDeletedFalse(request.name()).isPresent()) {
            throw new IllegalArgumentException("Role name already exists");
        }
        Role role = new Role();
        role.setName(request.name());
        role.setDescription(request.description());
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleResponse updateRole(UUID id, CreateRoleRequest request) {
        Role role = getRoleEntity(id);
        if (!role.getName().equals(request.name()) && roleRepository.findByNameAndIsDeletedFalse(request.name()).isPresent()) {
            throw new IllegalArgumentException("Role name already exists");
        }
        role.setName(request.name());
        role.setDescription(request.description());
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    @Transactional
    public void assignPermissions(UUID id, AssignPermissionsRequest request) {
        Role role = getRoleEntity(id);
        rolePermissionRepository.deleteByRoleId(role.getId());

        for (UUID permId : request.permissionIds()) {
            Permission permission = permissionRepository.findByIdAndIsDeletedFalse(permId)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found: " + permId));
            RolePermission rp = new RolePermission();
            rp.setRole(role);
            rp.setPermission(permission);
            rolePermissionRepository.save(rp);
        }
    }

    @Override
    @Transactional
    public void deleteRole(UUID id) {
        Role role = getRoleEntity(id);
        role.setDeleted(true);
        roleRepository.save(role);
    }

    private Role getRoleEntity(UUID id) {
        return roleRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Role not found"));
    }
}
