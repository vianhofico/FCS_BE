package com.fcs.be.modules.iam.service.impl;

import com.fcs.be.modules.iam.dto.request.CreateRoleRequest;
import com.fcs.be.modules.iam.dto.request.AssignPermissionsRequest;
import com.fcs.be.modules.iam.dto.response.RoleResponse;
import com.fcs.be.modules.iam.entity.Permission;
import com.fcs.be.modules.iam.entity.Role;
import com.fcs.be.modules.iam.repository.PermissionRepository;
import com.fcs.be.modules.iam.repository.RoleRepository;
import com.fcs.be.modules.iam.repository.RolePermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RoleServiceImplTest {

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    private CreateRoleRequest validRequest;

    @BeforeEach
    void setUp() {
        rolePermissionRepository.deleteAll();
        roleRepository.deleteAll();
        permissionRepository.deleteAll();

        validRequest = new CreateRoleRequest(
            "ADMIN",
            "Administrator role"
        );
    }

    @Test
    void testCreateRoleSuccess() {
        RoleResponse response = roleService.createRole(validRequest);

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals("ADMIN", response.name());
        assertEquals("Administrator role", response.description());
    }

    @Test
    void testCreateRoleWithDuplicateName() {
        roleService.createRole(validRequest);

        assertThrows(IllegalArgumentException.class, () -> roleService.createRole(validRequest));
    }

    @Test
    void testGetRolesSuccess() {
        roleService.createRole(validRequest);
        roleService.createRole(new CreateRoleRequest("USER", "User role"));

        List<RoleResponse> responses = roleService.getRoles();

        assertEquals(2, responses.size());
    }

    @Test
    void testUpdateRoleSuccess() {
        RoleResponse created = roleService.createRole(validRequest);

        CreateRoleRequest updateRequest = new CreateRoleRequest("ADMIN_V2", "Updated admin role");
        RoleResponse updated = roleService.updateRole(created.id(), updateRequest);

        assertEquals("ADMIN_V2", updated.name());
    }

    @Test
    void testAssignPermissionsSuccess() {
        RoleResponse role = roleService.createRole(validRequest);

        Permission perm1 = new Permission();
        perm1.setCode("VIEW_PRODUCT");
        perm1.setName("View Product");
        permissionRepository.save(perm1);

        AssignPermissionsRequest request = new AssignPermissionsRequest(List.of(perm1.getId()));
        roleService.assignPermissions(role.id(), request);

        long assignedCount = rolePermissionRepository.findByRoleId(role.id()).size();
        assertEquals(1, assignedCount);
    }

    @Test
    void testDeleteRoleSuccess() {
        RoleResponse created = roleService.createRole(validRequest);

        roleService.deleteRole(created.id());

        List<RoleResponse> responses = roleService.getRoles();
        assertEquals(0, responses.size());
    }
}
