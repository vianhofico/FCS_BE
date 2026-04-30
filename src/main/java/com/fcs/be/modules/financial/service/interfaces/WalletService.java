package com.fcs.be.modules.financial.service.interfaces;

import com.fcs.be.common.enums.WalletTransactionType;
import com.fcs.be.modules.financial.dto.request.UpdateWalletRequest;
import com.fcs.be.modules.financial.dto.response.WalletResponse;
import java.util.List;
import java.util.UUID;

public interface WalletService {

    List<WalletResponse> getWallets();

    WalletResponse getWallet(UUID id);

    WalletResponse updateWallet(UUID id, UpdateWalletRequest request);

    void recordTransaction(UUID walletId, WalletTransactionType type, java.math.BigDecimal amount, String description, String referenceType, UUID referenceId);
}