package com.fcs.be.modules.iam.service.interfaces;

import com.fcs.be.modules.iam.dto.request.AssignPermissionsRequest;
import com.fcs.be.modules.iam.dto.request.CreateRoleRequest;
import com.fcs.be.modules.iam.dto.response.RoleResponse;
import java.util.List;
import java.util.UUID;

public interface RoleService {

    List<RoleResponse> getRoles();

    RoleResponse createRole(CreateRoleRequest request);

    RoleResponse updateRole(UUID id, CreateRoleRequest request);

    void assignPermissions(UUID id, AssignPermissionsRequest request);

    void deleteRole(UUID id);
}
