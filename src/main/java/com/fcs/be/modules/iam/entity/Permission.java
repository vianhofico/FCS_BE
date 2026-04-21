package com.fcs.be.modules.iam.entity;

import com.fcs.be.common.entity.SoftDeleteEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "permissions")
public class Permission extends SoftDeleteEntity {

    @Column(name = "code", nullable = false, unique = true, length = 120)
    private String code;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "module", length = 80)
    private String module;
}
