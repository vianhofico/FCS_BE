package com.fcs.be.modules.catalog.repository;

import com.fcs.be.modules.catalog.entity.SystemSetting;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, UUID> {

    List<SystemSetting> findByIsDeletedFalseOrderByCreatedAtDesc();

    Optional<SystemSetting> findByIdAndIsDeletedFalse(UUID id);
}
