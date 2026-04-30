package com.fcs.be.modules.financial.service.impl;

import com.fcs.be.common.enums.WalletTransactionStatus;
import com.fcs.be.common.enums.WalletTransactionType;
import com.fcs.be.modules.financial.dto.request.UpdateWalletRequest;
import com.fcs.be.modules.financial.dto.response.WalletResponse;
import com.fcs.be.modules.financial.entity.Wallet;
import com.fcs.be.modules.financial.entity.WalletTransaction;
import com.fcs.be.modules.financial.mapper.WalletMapper;
import com.fcs.be.modules.financial.repository.WalletRepository;
import com.fcs.be.modules.financial.repository.WalletTransactionRepository;
import com.fcs.be.modules.financial.service.interfaces.WalletService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletMapper walletMapper;

    public WalletServiceImpl(
        WalletRepository walletRepository,
        WalletTransactionRepository walletTransactionRepository,
        WalletMapper walletMapper
    ) {
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.walletMapper = walletMapper;
    }

    @Override
    public List<WalletResponse> getWallets() {
        return walletRepository.findByIsDeletedFalseOrderByCreatedAtDesc()
            .stream()
            .map(walletMapper::toWalletResponse)
            .toList();
    }

    @Override
    public WalletResponse getWallet(UUID id) {
        return walletMapper.toWalletResponse(getWalletEntity(id));
    }

    @Override
    @Transactional
    public WalletResponse updateWallet(UUID id, UpdateWalletRequest request) {
        Wallet wallet = getWalletEntity(id);
        wallet.setAvailableBalance(request.availableBalance());
        wallet.setBankName(request.bankName());
        wallet.setBankAccountName(request.bankAccountName());
        wallet.setBankAccountNumber(request.bankAccountNumber());
        return walletMapper.toWalletResponse(walletRepository.save(wallet));
    }

    private Wallet getWalletEntity(UUID id) {
        return walletRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));
    }

    @Override
    @Transactional
    public void recordTransaction(UUID walletId, WalletTransactionType type, BigDecimal amount, String description, String referenceType, UUID referenceId) {
        Wallet wallet = getWalletEntity(walletId);

        // Update balance based on transaction type
        if (type == WalletTransactionType.SALE_REVENUE || type == WalletTransactionType.REFUND || type == WalletTransactionType.WITHDRAWAL_RELEASE || type == WalletTransactionType.ADJUSTMENT) {
            wallet.setBalance(wallet.getBalance().add(amount));
            if (type != WalletTransactionType.WITHDRAWAL_RELEASE) {
                wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
            } else {
                // When releasing a hold, only available balance goes up, total balance unchanged
                wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
                wallet.setBalance(wallet.getBalance());
            }
        } else if (type == WalletTransactionType.WITHDRAWAL_HOLD) {
            if (wallet.getAvailableBalance().compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient available balance");
            }
            wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amount));
        } else if (type == WalletTransactionType.WITHDRAWAL_PAID) {
            wallet.setBalance(wallet.getBalance().subtract(amount));
        } else if (type == WalletTransactionType.FEE) {
             if (wallet.getAvailableBalance().compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient available balance for fee");
             }
             wallet.setBalance(wallet.getBalance().subtract(amount));
             wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amount));
        }

        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
            .wallet(wallet)
            .amount(amount)
            .type(type)
            .status(WalletTransactionStatus.POSTED)
            .description(description)
            .referenceType(referenceType)
            .referenceId(referenceId)
            .idempotencyKey(UUID.randomUUID().toString())
            .build();
        walletTransactionRepository.save(transaction);
    }
}