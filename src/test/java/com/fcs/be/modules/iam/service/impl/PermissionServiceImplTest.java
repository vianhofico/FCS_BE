package com.fcs.be.modules.iam.service.impl;

import com.fcs.be.modules.iam.dto.request.CreatePermissionRequest;
import com.fcs.be.modules.iam.dto.response.PermissionResponse;
import com.fcs.be.modules.iam.entity.Permission;
import com.fcs.be.modules.iam.repository.PermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PermissionServiceImplTest {

    @Autowired
    private PermissionServiceImpl permissionService;

    @Autowired
    private PermissionRepository permissionRepository;

    private CreatePermissionRequest validRequest;

    @BeforeEach
    void setUp() {
        permissionRepository.deleteAll();
        validRequest = new CreatePermissionRequest(
            "VIEW_PRODUCT",
            "View Product",
            "Can view products",
            "Product"
        );
    }

    @Test
    void testCreatePermissionSuccess() {
        PermissionResponse response = permissionService.createPermission(validRequest);

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals("VIEW_PRODUCT", response.code());
        assertEquals("View Product", response.name());

        Permission saved = permissionRepository.findByIdAndIsDeletedFalse(response.id()).orElse(null);
        assertNotNull(saved);
    }

    @Test
    void testGetPermissionsSuccess() {
        permissionService.createPermission(validRequest);
        permissionService.createPermission(new CreatePermissionRequest("EDIT_PRODUCT", "Edit Product", "Can edit products", "Product"));

        List<PermissionResponse> responses = permissionService.getPermissions();

        assertEquals(2, responses.size());
    }

    @Test
    void testUpdatePermissionSuccess() {
        PermissionResponse created = permissionService.createPermission(validRequest);

        CreatePermissionRequest updateRequest = new CreatePermissionRequest(
            "VIEW_PRODUCT_V2",
            "View Product V2",
            "Updated description",
            "Product"
        );

        PermissionResponse updated = permissionService.updatePermission(created.id(), updateRequest);

        assertEquals("VIEW_PRODUCT_V2", updated.code());
        assertEquals("View Product V2", updated.name());
    }

    @Test
    void testDeletePermissionSuccess() {
        PermissionResponse created = permissionService.createPermission(validRequest);

        permissionService.deletePermission(created.id());

        List<PermissionResponse> responses = permissionService.getPermissions();
        assertEquals(0, responses.size());
    }

    @Test
    void testGetPermissionsEmpty() {
        List<PermissionResponse> responses = permissionService.getPermissions();
        assertTrue(responses.isEmpty());
    }
}
