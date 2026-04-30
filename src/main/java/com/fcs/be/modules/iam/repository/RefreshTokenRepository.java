package com.fcs.be.modules.iam.repository;

import com.fcs.be.modules.iam.entity.RefreshToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHashAndRevokedAtIsNull(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revokedAt = :now, r.revokeReason = :reason WHERE r.user.id = :userId AND r.revokedAt IS NULL")
    void revokeAllByUserId(UUID userId, String reason, java.time.Instant now);
}
