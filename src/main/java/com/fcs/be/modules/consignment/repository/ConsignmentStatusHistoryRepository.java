package com.fcs.be.modules.consignment.repository;

import com.fcs.be.modules.consignment.entity.ConsignmentStatusHistory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsignmentStatusHistoryRepository extends JpaRepository<ConsignmentStatusHistory, UUID> {
}
