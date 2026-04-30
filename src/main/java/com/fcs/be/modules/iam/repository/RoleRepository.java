package com.fcs.be.modules.iam.repository;

import com.fcs.be.modules.iam.entity.Role;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    List<Role> findByIsDeletedFalse();

    Optional<Role> findByIdAndIsDeletedFalse(UUID id);

    Optional<Role> findByNameAndIsDeletedFalse(String name);
}
