package com.fcs.be.modules.financial.service.impl;

import com.fcs.be.common.enums.WalletTransactionType;
import com.fcs.be.common.enums.WithdrawalStatus;
import com.fcs.be.modules.financial.dto.request.CreateWithdrawalRequest;
import com.fcs.be.modules.financial.dto.request.UpdateWithdrawalStatusRequest;
import com.fcs.be.modules.financial.dto.response.WithdrawalRequestResponse;
import com.fcs.be.modules.financial.entity.Wallet;
import com.fcs.be.modules.financial.entity.WithdrawalRequest;
import com.fcs.be.modules.financial.entity.WithdrawalStatusHistory;
import com.fcs.be.modules.financial.mapper.WithdrawalMapper;
import com.fcs.be.modules.financial.repository.WalletRepository;
import com.fcs.be.modules.financial.repository.WithdrawalRequestRepository;
import com.fcs.be.modules.financial.repository.WithdrawalStatusHistoryRepository;
import com.fcs.be.modules.financial.service.interfaces.WalletService;
import com.fcs.be.modules.financial.service.interfaces.WithdrawalService;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WithdrawalServiceImpl implements WithdrawalService {

    private final WithdrawalRequestRepository withdrawalRequestRepository;
    private final WithdrawalStatusHistoryRepository withdrawalStatusHistoryRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final WithdrawalMapper withdrawalMapper;

    public WithdrawalServiceImpl(
        WithdrawalRequestRepository withdrawalRequestRepository,
        WithdrawalStatusHistoryRepository withdrawalStatusHistoryRepository,
        WalletRepository walletRepository,
        UserRepository userRepository,
        WalletService walletService,
        WithdrawalMapper withdrawalMapper
    ) {
        this.withdrawalRequestRepository = withdrawalRequestRepository;
        this.withdrawalStatusHistoryRepository = withdrawalStatusHistoryRepository;
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.withdrawalMapper = withdrawalMapper;
    }

    @Override
    public List<WithdrawalRequestResponse> getWithdrawals() {
        return withdrawalRequestRepository.findByIsDeletedFalseOrderByCreatedAtDesc()
            .stream()
            .map(withdrawalMapper::toResponse)
            .toList();
    }

    @Override
    public WithdrawalRequestResponse getWithdrawal(UUID id) {
        return withdrawalMapper.toResponse(getWithdrawalEntity(id));
    }

    @Override
    @Transactional
    public WithdrawalRequestResponse createWithdrawal(CreateWithdrawalRequest request) {
        Wallet wallet = walletRepository.findByIdAndIsDeletedFalse(request.walletId())
            .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        if (wallet.getAvailableBalance().compareTo(request.amount()) < 0) {
            throw new IllegalStateException("Insufficient available balance");
        }

        WithdrawalRequest wr = WithdrawalRequest.builder()
            .wallet(wallet)
            .amount(request.amount())
            .status(WithdrawalStatus.PENDING)
            .requestCode("WD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .bankSnapshotName(wallet.getBankAccountName())
            .bankSnapshotNumber(wallet.getBankAccountNumber())
            .bankSnapshotBranch(wallet.getBankName())
            .build();

        WithdrawalRequest saved = withdrawalRequestRepository.save(wr);

        walletService.recordTransaction(
            wallet.getId(),
            WalletTransactionType.WITHDRAWAL_HOLD,
            request.amount(),
            "Hold for withdrawal request " + saved.getRequestCode(),
            "WITHDRAWAL",
            saved.getId()
        );

        appendStatusHistory(saved, null, WithdrawalStatus.PENDING, "Withdrawal requested");
        return withdrawalMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public WithdrawalRequestResponse updateStatus(UUID id, UpdateWithdrawalStatusRequest request, UUID reviewerId) {
        WithdrawalRequest wr = getWithdrawalEntity(id);
        WithdrawalStatus oldStatus = wr.getStatus();

        if (oldStatus == WithdrawalStatus.PAID || oldStatus == WithdrawalStatus.REJECTED || oldStatus == WithdrawalStatus.CANCELLED) {
            throw new IllegalStateException("Withdrawal is in terminal state and cannot be updated");
        }

        wr.setStatus(request.status());

        if (reviewerId != null) {
            User reviewer = userRepository.findByIdAndIsDeletedFalse(reviewerId)
                .orElseThrow(() -> new EntityNotFoundException("Reviewer not found"));
            wr.setReviewedBy(reviewer);
            wr.setReviewedAt(Instant.now());
        }

        if (request.status() == WithdrawalStatus.REJECTED) {
            wr.setRejectReason(request.rejectReason());
            walletService.recordTransaction(
                wr.getWallet().getId(),
                WalletTransactionType.WITHDRAWAL_RELEASE,
                wr.getAmount(),
                "Release hold from rejected withdrawal " + wr.getRequestCode(),
                "WITHDRAWAL",
                wr.getId()
            );
        } else if (request.status() == WithdrawalStatus.PAID) {
            wr.setTransferReference(request.transferReference());
            wr.setReceiptImageUrl(request.receiptImageUrl());
            wr.setTransferredAt(Instant.now());
            walletService.recordTransaction(
                wr.getWallet().getId(),
                WalletTransactionType.WITHDRAWAL_PAID,
                wr.getAmount(),
                "Withdrawal paid " + wr.getRequestCode(),
                "WITHDRAWAL",
                wr.getId()
            );
        }

        WithdrawalRequest saved = withdrawalRequestRepository.save(wr);
        appendStatusHistory(saved, oldStatus, request.status(), request.rejectReason());
        return withdrawalMapper.toResponse(saved);
    }

    private WithdrawalRequest getWithdrawalEntity(UUID id) {
        return withdrawalRequestRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Withdrawal request not found"));
    }

    @Transactional
    private void appendStatusHistory(WithdrawalRequest wr, WithdrawalStatus fromStatus, WithdrawalStatus toStatus, String reason) {
        WithdrawalStatusHistory history = WithdrawalStatusHistory.builder()
            .withdrawalRequest(wr)
            .fromStatus(fromStatus == null ? null : fromStatus.name())
            .toStatus(toStatus.name())
            .reason(reason)
            .build();
        withdrawalStatusHistoryRepository.save(history);
    }
}
