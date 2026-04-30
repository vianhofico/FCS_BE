package com.fcs.be.modules.order.service.impl;

import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.common.enums.UserStatus;
import com.fcs.be.common.enums.VoucherDiscountType;
import com.fcs.be.common.enums.VoucherStatus;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.order.dto.request.CreateVoucherRequest;
import com.fcs.be.modules.order.dto.response.VoucherResponse;
import com.fcs.be.modules.order.entity.Voucher;
import com.fcs.be.modules.order.repository.VoucherRepository;
import com.fcs.be.modules.order.repository.VoucherUsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VoucherServiceImplTest {

    @Autowired
    private VoucherServiceImpl voucherService;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private VoucherUsageRepository voucherUsageRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private CreateVoucherRequest validRequest;

    @BeforeEach
    void setUp() {
        voucherUsageRepository.deleteAll();
        voucherRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
            .username("testuser")
            .email("test@example.com")
            .passwordHash("hashed")
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(testUser);

        validRequest = new CreateVoucherRequest(
            "SAVE10",
            VoucherDiscountType.PERCENT,
            new BigDecimal("10"),
            new BigDecimal("100000"),
            new BigDecimal("50000"),
            Instant.now(),
            Instant.now().plusSeconds(86400),
            100,
            VoucherStatus.ACTIVE
        );
    }

    @Test
    void testCreateVoucherSuccess() {
        VoucherResponse response = voucherService.createVoucher(validRequest);

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals("SAVE10", response.code());
        assertEquals(VoucherStatus.ACTIVE, response.status());
    }

    @Test
    void testCreateVoucherWithDuplicateCode() {
        voucherService.createVoucher(validRequest);

        assertThrows(IllegalArgumentException.class, () -> voucherService.createVoucher(validRequest));
    }

    @Test
    void testGetVouchersSuccess() {
        voucherService.createVoucher(validRequest);
        voucherService.createVoucher(new CreateVoucherRequest(
            "SAVE20",
            VoucherDiscountType.PERCENT,
            new BigDecimal("20"),
            null, null, null, null, null,
            VoucherStatus.ACTIVE
        ));

        List<VoucherResponse> responses = voucherService.getVouchers();

        assertEquals(2, responses.size());
    }

    @Test
    void testValidateAndCalculateDiscountPercent() {
        voucherService.createVoucher(validRequest);

        BigDecimal discount = voucherService.validateAndCalculateDiscount(
            "SAVE10",
            testUser.getId(),
            new BigDecimal("500000")
        );

        assertEquals(new BigDecimal("50000"), discount);
    }

    @Test
    void testValidateAndCalculateDiscountFixed() {
        CreateVoucherRequest fixedRequest = new CreateVoucherRequest(
            "FIXED100",
            VoucherDiscountType.FIXED_AMOUNT,
            new BigDecimal("100000"),
            new BigDecimal("500000"),
            null,
            Instant.now(),
            Instant.now().plusSeconds(86400),
            10,
            VoucherStatus.ACTIVE
        );

        voucherService.createVoucher(fixedRequest);

        BigDecimal discount = voucherService.validateAndCalculateDiscount(
            "FIXED100",
            testUser.getId(),
            new BigDecimal("1000000")
        );

        assertEquals(new BigDecimal("100000"), discount);
    }

    @Test
    void testValidateVoucherWithInvalidCode() {
        assertThrows(Exception.class, () -> voucherService.validateAndCalculateDiscount(
            "INVALID",
            testUser.getId(),
            new BigDecimal("500000")
        ));
    }

    @Test
    void testGetVoucherSuccess() {
        VoucherResponse created = voucherService.createVoucher(validRequest);

        VoucherResponse response = voucherService.getVoucher(created.id());

        assertNotNull(response);
        assertEquals(created.code(), response.code());
    }
}
