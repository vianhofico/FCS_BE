package com.fcs.be.modules.financial.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.financial.dto.request.UpdateWalletRequest;
import com.fcs.be.modules.financial.dto.response.WalletResponse;
import com.fcs.be.modules.financial.service.interfaces.FinancialService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/financial/wallets")
public class FinancialController {

    private final FinancialService financialService;

    public FinancialController(FinancialService financialService) {
        this.financialService = financialService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WalletResponse>>> getWallets() {
        return ResponseEntity.ok(ApiResponse.ok("Fetched wallets", financialService.getWallets()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WalletResponse>> getWallet(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Fetched wallet", financialService.getWallet(id)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WalletResponse>> updateWallet(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateWalletRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Wallet updated", financialService.updateWallet(id, request)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }
}
