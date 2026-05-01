package com.fcs.be.modules.order.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.order.dto.request.CreateVoucherRequest;
import com.fcs.be.modules.order.dto.response.VoucherResponse;
import com.fcs.be.modules.order.service.interfaces.VoucherService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
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
@RequestMapping("/api/v1/vouchers")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VoucherResponse>> createVoucher(
        @Valid @RequestBody CreateVoucherRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Voucher created", voucherService.createVoucher(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<VoucherResponse>>> getVouchers(
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched vouchers", voucherService.getVouchers(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VoucherResponse>> getVoucher(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched voucher", voucherService.getVoucher(id)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<VoucherResponse>> updateStatus(
        @PathVariable UUID id,
        @Valid @RequestBody com.fcs.be.modules.order.dto.request.UpdateVoucherStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Voucher status updated", voucherService.updateStatus(id, request.status())));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<BigDecimal>> validateVoucher(
        @RequestParam String code,
        @RequestParam UUID userId,
        @RequestParam BigDecimal orderAmount
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Voucher validated",
            voucherService.validateAndCalculateDiscount(code, userId, orderAmount)));
    }
}

