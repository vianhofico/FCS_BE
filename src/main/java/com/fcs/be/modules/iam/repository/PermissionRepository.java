package com.fcs.be.modules.iam.repository;

import com.fcs.be.modules.iam.entity.Permission;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    List<Permission> findByIsDeletedFalse();

    Optional<Permission> findByIdAndIsDeletedFalse(UUID id);
}
