package com.fcs.be.modules.order.service.impl;

import com.fcs.be.common.enums.VoucherDiscountType;
import com.fcs.be.common.enums.VoucherStatus;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.order.dto.request.CreateVoucherRequest;
import com.fcs.be.modules.order.dto.response.VoucherResponse;
import com.fcs.be.modules.order.entity.Order;
import com.fcs.be.modules.order.entity.Voucher;
import com.fcs.be.modules.order.entity.VoucherUsage;
import com.fcs.be.modules.order.mapper.VoucherMapper;
import com.fcs.be.modules.order.repository.OrderRepository;
import com.fcs.be.modules.order.repository.VoucherRepository;
import com.fcs.be.modules.order.repository.VoucherUsageRepository;
import com.fcs.be.modules.order.service.interfaces.VoucherService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherUsageRepository voucherUsageRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final VoucherMapper voucherMapper;

    public VoucherServiceImpl(
        VoucherRepository voucherRepository,
        VoucherUsageRepository voucherUsageRepository,
        UserRepository userRepository,
        OrderRepository orderRepository,
        VoucherMapper voucherMapper
    ) {
        this.voucherRepository = voucherRepository;
        this.voucherUsageRepository = voucherUsageRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.voucherMapper = voucherMapper;
    }

    @Override
    @Transactional
    public VoucherResponse createVoucher(CreateVoucherRequest request) {
        if (voucherRepository.findByCodeAndIsDeletedFalse(request.code()).isPresent()) {
            throw new IllegalArgumentException("Voucher code already exists");
        }
        Voucher voucher = Voucher.builder()
            .code(request.code())
            .discountType(request.discountType())
            .value(request.value())
            .minOrderValue(request.minOrderValue())
            .maxDiscount(request.maxDiscount())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .usageLimit(request.usageLimit())
            .usedCount(0)
            .status(request.status())
            .build();
        return voucherMapper.toResponse(voucherRepository.save(voucher));
    }

    @Override
    public PageResponse<VoucherResponse> getVouchers(Pageable pageable) {
        return PageResponse.of(
            voucherRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable)
                .map(voucherMapper::toResponse)
        );
    }

    @Override
    public VoucherResponse getVoucher(UUID id) {
        return voucherMapper.toResponse(voucherRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Voucher not found")));
    }

    @Override
    @Transactional
    public VoucherResponse updateStatus(UUID id, VoucherStatus status) {
        Voucher voucher = voucherRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Voucher not found"));
        voucher.setStatus(status);
        return voucherMapper.toResponse(voucherRepository.save(voucher));
    }

    @Override
    public BigDecimal validateAndCalculateDiscount(String code, UUID userId, BigDecimal orderAmount) {
        Voucher voucher = voucherRepository.findByCodeAndIsDeletedFalse(code)
            .orElseThrow(() -> new EntityNotFoundException("Voucher not found"));

        validateVoucherUsability(voucher, userId, orderAmount);

        return calculateDiscount(voucher, orderAmount);
    }

    @Override
    @Transactional
    public void applyVoucher(String code, UUID userId, UUID orderId) {
        Voucher voucher = voucherRepository.findByCodeAndIsDeletedFalse(code)
            .orElseThrow(() -> new EntityNotFoundException("Voucher not found"));
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        VoucherUsage usage = VoucherUsage.builder()
            .voucher(voucher)
            .user(user)
            .order(order)
            .build();
        voucherUsageRepository.save(usage);

        voucher.setUsedCount(voucher.getUsedCount() + 1);
        voucherRepository.save(voucher);
    }

    private void validateVoucherUsability(Voucher voucher, UUID userId, BigDecimal orderAmount) {
        if (voucher.getStatus() != VoucherStatus.ACTIVE) {
            throw new IllegalStateException("Voucher is not active");
        }
        Instant now = Instant.now();
        if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
            throw new IllegalStateException("Voucher is not yet valid");
        }
        if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
            throw new IllegalStateException("Voucher has expired");
        }
        if (voucher.getUsageLimit() != null
            && voucherUsageRepository.countByVoucherIdAndIsDeletedFalse(voucher.getId()) >= voucher.getUsageLimit()) {
            throw new IllegalStateException("Voucher usage limit reached");
        }
        if (voucherUsageRepository.findByVoucherIdAndUserIdAndIsDeletedFalse(voucher.getId(), userId).isPresent()) {
            throw new IllegalStateException("You have already used this voucher");
        }
        if (voucher.getMinOrderValue() != null && orderAmount.compareTo(voucher.getMinOrderValue()) < 0) {
            throw new IllegalStateException("Order amount is below the minimum required for this voucher");
        }
    }

    private BigDecimal calculateDiscount(Voucher voucher, BigDecimal orderAmount) {
        BigDecimal discount;
        if (voucher.getDiscountType() == VoucherDiscountType.PERCENT) {
            discount = orderAmount.multiply(voucher.getValue())
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        } else {
            discount = voucher.getValue();
        }
        if (voucher.getMaxDiscount() != null && discount.compareTo(voucher.getMaxDiscount()) > 0) {
            discount = voucher.getMaxDiscount();
        }
        return discount.min(orderAmount);
    }

}
