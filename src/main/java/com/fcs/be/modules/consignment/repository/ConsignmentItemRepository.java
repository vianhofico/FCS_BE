package com.fcs.be.modules.consignment.repository;

import com.fcs.be.modules.consignment.entity.ConsignmentItem;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsignmentItemRepository extends JpaRepository<ConsignmentItem, UUID> {

    Optional<ConsignmentItem> findByIdAndIsDeletedFalse(UUID id);
}
