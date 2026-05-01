package com.fcs.be.modules.return_request.repository;

import com.fcs.be.modules.return_request.entity.ReturnStatusHistory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnStatusHistoryRepository extends JpaRepository<ReturnStatusHistory, UUID> {
}
