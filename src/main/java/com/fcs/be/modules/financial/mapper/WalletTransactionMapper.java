package com.fcs.be.modules.financial.mapper;

import com.fcs.be.modules.financial.dto.response.WalletTransactionResponse;
import com.fcs.be.modules.financial.entity.WalletTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WalletTransactionMapper {

    @Mapping(target = "walletId", source = "wallet.id")
    @Mapping(target = "orderId", source = "order.id")
    WalletTransactionResponse toResponse(WalletTransaction transaction);
}