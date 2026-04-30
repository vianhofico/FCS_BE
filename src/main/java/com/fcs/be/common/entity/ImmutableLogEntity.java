package com.fcs.be.common.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class ImmutableLogEntity extends BaseEntity {
}
