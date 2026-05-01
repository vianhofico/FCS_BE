package com.fcs.be.modules.return_request.repository;

import com.fcs.be.modules.return_request.entity.ReturnRequest;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, UUID>, JpaSpecificationExecutor<ReturnRequest> {
    Optional<ReturnRequest> findByIdAndIsDeletedFalse(UUID id);
}
