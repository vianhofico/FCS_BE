package com.fcs.be.modules.order.service.interfaces;

import com.fcs.be.common.enums.VoucherStatus;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.order.dto.request.CreateVoucherRequest;
import com.fcs.be.modules.order.dto.response.VoucherResponse;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface VoucherService {

    VoucherResponse createVoucher(CreateVoucherRequest request);

    PageResponse<VoucherResponse> getVouchers(Pageable pageable);

    VoucherResponse getVoucher(UUID id);

    VoucherResponse updateStatus(UUID id, VoucherStatus status);

    BigDecimal validateAndCalculateDiscount(String code, UUID userId, BigDecimal orderAmount);

    void applyVoucher(String code, UUID userId, UUID orderId);
}

