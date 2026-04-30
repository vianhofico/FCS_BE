package com.fcs.be.modules.financial.mapper;

import com.fcs.be.modules.financial.dto.response.WalletResponse;
import com.fcs.be.modules.financial.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @Mapping(target = "userId", source = "user.id")
    WalletResponse toWalletResponse(Wallet wallet);
}