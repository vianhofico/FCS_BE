package com.fcs.be.modules.iam.service.interfaces;

import com.fcs.be.modules.iam.dto.request.CreatePermissionRequest;
import com.fcs.be.modules.iam.dto.response.PermissionResponse;
import java.util.List;
import java.util.UUID;

public interface PermissionService {

    List<PermissionResponse> getPermissions();

    PermissionResponse createPermission(CreatePermissionRequest request);

    PermissionResponse updatePermission(UUID id, CreatePermissionRequest request);

    void deletePermission(UUID id);
}
