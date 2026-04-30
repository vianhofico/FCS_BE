package com.fcs.be.modules.financial.mapper;

import com.fcs.be.modules.financial.dto.response.WalletResponse;
import com.fcs.be.modules.financial.entity.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public WalletResponse toWalletResponse(Wallet wallet) {
        return new WalletResponse(
            wallet.getId(),
            wallet.getUser().getId(),
            wallet.getBalance(),
            wallet.getAvailableBalance(),
            wallet.getBankName(),
            wallet.getBankAccountName(),
            wallet.getBankAccountNumber()
        );
    }
}