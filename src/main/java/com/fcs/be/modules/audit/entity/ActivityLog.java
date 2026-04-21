package com.fcs.be.modules.audit.entity;

import com.fcs.be.common.entity.ImmutableLogEntity;
import com.fcs.be.common.enums.ActivityAction;
import com.fcs.be.modules.iam.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "activity_logs")
public class ActivityLog extends ImmutableLogEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 30)
    private ActivityAction action;

    @Column(name = "entity_name", nullable = false, length = 120)
    private String entityName;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "old_values", length = 4000)
    private String oldValues;

    @Column(name = "new_values", length = 4000)
    private String newValues;

    @Column(name = "ip_address", length = 80)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;
}
