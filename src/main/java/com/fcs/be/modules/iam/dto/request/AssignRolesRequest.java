package com.fcs.be.modules.iam.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record AssignRolesRequest(
    @NotNull List<UUID> roleIds
) {}
