package com.fcs.be.modules.financial.service.impl;

import com.fcs.be.modules.financial.dto.request.UpdateWalletRequest;
import com.fcs.be.modules.financial.dto.response.WalletResponse;
import com.fcs.be.modules.financial.entity.Wallet;
import com.fcs.be.modules.financial.mapper.WalletMapper;
import com.fcs.be.modules.financial.repository.WalletRepository;
import com.fcs.be.modules.financial.service.interfaces.WalletService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    public WalletServiceImpl(WalletRepository walletRepository, WalletMapper walletMapper) {
        this.walletRepository = walletRepository;
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
}