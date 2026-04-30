package com.fcs.be.modules.financial.service.interfaces;

import com.fcs.be.modules.financial.dto.response.WalletTransactionResponse;
import java.util.List;
import java.util.UUID;

public interface WalletTransactionService {
    List<WalletTransactionResponse> getTransactionsByWallet(UUID walletId);
}
