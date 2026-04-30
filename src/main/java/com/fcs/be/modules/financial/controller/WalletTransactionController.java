package com.fcs.be.modules.financial.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.financial.dto.response.WalletTransactionResponse;
import com.fcs.be.modules.financial.service.interfaces.WalletTransactionService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/financial/wallets")
public class WalletTransactionController {

    private final WalletTransactionService walletTransactionService;

    public WalletTransactionController(WalletTransactionService walletTransactionService) {
        this.walletTransactionService = walletTransactionService;
    }

    @GetMapping("/{walletId}/transactions")
    public ResponseEntity<ApiResponse<List<WalletTransactionResponse>>> getTransactions(@PathVariable UUID walletId) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched wallet transactions", walletTransactionService.getTransactionsByWallet(walletId)));
    }
}
