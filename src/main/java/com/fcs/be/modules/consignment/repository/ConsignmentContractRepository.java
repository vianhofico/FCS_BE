package com.fcs.be.modules.consignment.repository;

import com.fcs.be.modules.consignment.entity.ConsignmentContract;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsignmentContractRepository extends JpaRepository<ConsignmentContract, UUID> {

    Optional<ConsignmentContract> findByIdAndIsDeletedFalse(UUID id);

    Optional<ConsignmentContract> findByRequestIdAndIsDeletedFalse(UUID requestId);
}
