package com.fcs.be.modules.iam.repository;

import com.fcs.be.modules.iam.entity.UserAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {

    List<UserAddress> findByUserIdAndIsDeletedFalseOrderByIsDefaultDesc(UUID userId);

    Optional<UserAddress> findByIdAndIsDeletedFalse(UUID id);

    @Modifying
    @Query("UPDATE UserAddress ua SET ua.isDefault = false WHERE ua.user.id = :userId")
    void clearDefaultAddresses(UUID userId);
}
