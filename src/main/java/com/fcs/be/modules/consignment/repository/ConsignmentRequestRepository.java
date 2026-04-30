package com.fcs.be.modules.consignment.repository;

import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.modules.consignment.entity.ConsignmentRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsignmentRequestRepository extends JpaRepository<ConsignmentRequest, UUID> {

    List<ConsignmentRequest> findByIsDeletedFalseOrderByCreatedAtDesc();

    List<ConsignmentRequest> findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(ConsignmentRequestStatus status);

    Optional<ConsignmentRequest> findByIdAndIsDeletedFalse(UUID id);
}
