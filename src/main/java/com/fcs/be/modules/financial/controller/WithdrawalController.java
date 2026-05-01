package com.fcs.be.modules.financial.controller;

import com.fcs.be.common.enums.WithdrawalStatus;
import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.financial.dto.request.CreateWithdrawalRequest;
import com.fcs.be.modules.financial.dto.request.UpdateWithdrawalStatusRequest;
import com.fcs.be.modules.financial.dto.request.WithdrawalFilterRequest;
import com.fcs.be.modules.financial.dto.response.WithdrawalRequestResponse;
import com.fcs.be.modules.financial.service.interfaces.WithdrawalService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/financial/withdrawals")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<WithdrawalRequestResponse>>> getWithdrawals(
        @RequestParam(required = false) UUID walletId,
        @RequestParam(required = false) WithdrawalStatus status,
        @RequestParam(required = false) Instant startDate,
        @RequestParam(required = false) Instant endDate,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        WithdrawalFilterRequest filter = new WithdrawalFilterRequest(walletId, status, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok("Fetched withdrawals", withdrawalService.getWithdrawals(filter, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WithdrawalRequestResponse>> getWithdrawal(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched withdrawal", withdrawalService.getWithdrawal(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WithdrawalRequestResponse>> createWithdrawal(
        @Valid @RequestBody CreateWithdrawalRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Withdrawal created", withdrawalService.createWithdrawal(request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<WithdrawalRequestResponse>> updateStatus(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateWithdrawalStatusRequest request
        // In a real app we'd get reviewerId from SecurityContext Holder here
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Withdrawal status updated", withdrawalService.updateStatus(id, request, null)));
    }
}

