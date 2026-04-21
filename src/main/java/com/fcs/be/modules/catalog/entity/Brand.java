package com.fcs.be.modules.catalog.entity;

import com.fcs.be.common.entity.SoftDeleteEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "brands")
public class Brand extends SoftDeleteEntity {

    @Column(name = "name", nullable = false, unique = true, length = 150)
    private String name;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;
}
