package com.fcs.be.modules.financial.service.impl;

import com.fcs.be.modules.financial.dto.response.WalletTransactionResponse;
import com.fcs.be.modules.financial.entity.WalletTransaction;
import com.fcs.be.modules.financial.mapper.WalletTransactionMapper;
import com.fcs.be.modules.financial.repository.WalletTransactionRepository;
import com.fcs.be.modules.financial.service.interfaces.WalletTransactionService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletTransactionMapper walletTransactionMapper;

    public WalletTransactionServiceImpl(
        WalletTransactionRepository walletTransactionRepository,
        WalletTransactionMapper walletTransactionMapper
    ) {
        this.walletTransactionRepository = walletTransactionRepository;
        this.walletTransactionMapper = walletTransactionMapper;
    }

    @Override
    public List<WalletTransactionResponse> getTransactionsByWallet(UUID walletId) {
        return walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(walletId)
            .stream()
            .map(walletTransactionMapper::toResponse)
            .toList();
    }
}
