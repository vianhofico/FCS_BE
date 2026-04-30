package com.fcs.be.modules.order.service.interfaces;

import com.fcs.be.modules.order.dto.request.CreateVoucherRequest;
import com.fcs.be.modules.order.dto.response.VoucherResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface VoucherService {

    VoucherResponse createVoucher(CreateVoucherRequest request);

    List<VoucherResponse> getVouchers();

    VoucherResponse getVoucher(UUID id);

    BigDecimal validateAndCalculateDiscount(String code, UUID userId, BigDecimal orderAmount);

    void applyVoucher(String code, UUID userId, UUID orderId);
}
