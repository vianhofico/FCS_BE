package com.fcs.be.modules.iam.repository;

import com.fcs.be.common.enums.AuthProvider;
import com.fcs.be.modules.iam.entity.AuthIdentity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthIdentityRepository extends JpaRepository<AuthIdentity, UUID> {

    Optional<AuthIdentity> findByUserIdAndProvider(UUID userId, AuthProvider provider);

    Optional<AuthIdentity> findByProviderEmailAndProvider(String email, AuthProvider provider);
}
