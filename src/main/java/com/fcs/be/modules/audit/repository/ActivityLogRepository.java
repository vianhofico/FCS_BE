package com.fcs.be.modules.audit.repository;

import com.fcs.be.modules.audit.entity.ActivityLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {

    List<ActivityLog> findAllByOrderByCreatedAtDesc();
}
