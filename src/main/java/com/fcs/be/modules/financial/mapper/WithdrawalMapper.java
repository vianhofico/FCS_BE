package com.fcs.be.modules.financial.mapper;

import com.fcs.be.modules.financial.dto.response.WithdrawalRequestResponse;
import com.fcs.be.modules.financial.entity.WithdrawalRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WithdrawalMapper {

    @Mapping(target = "walletId", source = "wallet.id")
    @Mapping(target = "reviewedBy", source = "reviewedBy.id")
    WithdrawalRequestResponse toResponse(WithdrawalRequest withdrawalRequest);
}