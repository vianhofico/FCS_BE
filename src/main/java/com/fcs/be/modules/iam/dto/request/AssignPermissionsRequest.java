package com.fcs.be.modules.iam.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record AssignPermissionsRequest(
    @NotNull List<UUID> permissionIds
) {}
