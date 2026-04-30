package com.fcs.be.modules.iam.repository;

import com.fcs.be.modules.iam.entity.UserAddress;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {

    Optional<UserAddress> findByIdAndIsDeletedFalse(UUID id);
}
