package com.fcs.be.modules.iam.entity;

import com.fcs.be.common.entity.BaseEntity;
import com.fcs.be.common.enums.AuthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "auth_identities",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_auth_identities_provider_user", columnNames = {"provider", "provider_user_id"}),
        @UniqueConstraint(name = "uk_auth_identities_user_provider", columnNames = {"user_id", "provider"})
    }
)
public class AuthIdentity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    private AuthProvider provider;

    @Column(name = "provider_user_id", length = 255)
    private String providerUserId;

    @Column(name = "provider_email", length = 180)
    private String providerEmail;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "is_primary", nullable = false)
    private boolean primary = true;
}
