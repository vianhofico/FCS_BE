package com.fcs.be.modules.product.repository;

import com.fcs.be.common.enums.MediaOwnerType;
import com.fcs.be.modules.product.entity.MediaAsset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID> {

    Optional<MediaAsset> findByIdAndIsDeletedFalse(UUID id);

    List<MediaAsset> findByOwnerTypeAndOwnerIdAndIsDeletedFalseOrderByDisplayOrderAsc(
        MediaOwnerType ownerType, UUID ownerId
    );
}
