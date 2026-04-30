package com.fcs.be.modules.consignment.service.impl;

import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.common.enums.UserStatus;
import com.fcs.be.modules.consignment.dto.request.CreateConsignmentRequest;
import com.fcs.be.modules.consignment.dto.response.ConsignmentResponse;
import com.fcs.be.modules.consignment.repository.ConsignmentItemRepository;
import com.fcs.be.modules.consignment.repository.ConsignmentRequestRepository;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ConsignmentServiceImplTest {

    @Autowired
    private ConsignmentServiceImpl consignmentService;

    @Autowired
    private ConsignmentRequestRepository consignmentRequestRepository;

    @Autowired
    private ConsignmentItemRepository consignmentItemRepository;

    @Autowired
    private UserRepository userRepository;

    private User consignorUser;
    private User commissionerUser;

    @BeforeEach
    void setUp() {
        consignmentItemRepository.deleteAll();
        consignmentRequestRepository.deleteAll();
        userRepository.deleteAll();

        consignorUser = User.builder()
            .username("consignor")
            .email("consignor@example.com")
            .passwordHash("hashed")
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(consignorUser);

        commissionerUser = User.builder()
            .username("commissioner")
            .email("commissioner@example.com")
            .passwordHash("hashed")
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(commissionerUser);
    }

    @Test
    void testCreateConsignmentRequestSuccess() {
        CreateConsignmentRequest request = new CreateConsignmentRequest(
            consignorUser.getId(),
            "CONS001",
            ConsignmentRequestStatus.DRAFT,
            "Request consignment for summer collection"
        );

        ConsignmentResponse response = consignmentService.createConsignment(request);

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(ConsignmentRequestStatus.DRAFT, response.status());
        assertEquals(consignorUser.getId(), response.consignorId());
    }

    @Test
    void testGetConsignmentRequestSuccess() {
        CreateConsignmentRequest request = new CreateConsignmentRequest(
            consignorUser.getId(),
            "CONS001",
            ConsignmentRequestStatus.DRAFT,
            "Request consignment"
        );

        ConsignmentResponse created = consignmentService.createConsignment(request);

        ConsignmentResponse response = consignmentService.getConsignment(created.id());

        assertNotNull(response);
        assertEquals(created.id(), response.id());
    }

    @Test
    void testGetRequestsByConsignor() {
        CreateConsignmentRequest request = new CreateConsignmentRequest(
            consignorUser.getId(),
            "CONS001",
            ConsignmentRequestStatus.DRAFT,
            "Request consignment"
        );

        consignmentService.createConsignment(request);

        List<ConsignmentResponse> responses = consignmentService.getConsignments(ConsignmentRequestStatus.DRAFT);

        assertEquals(1, responses.size());
    }

    @Test
    void testUpdateRequestStatus() {
        CreateConsignmentRequest request = new CreateConsignmentRequest(
            consignorUser.getId(),
            "CONS001",
            ConsignmentRequestStatus.DRAFT,
            "Request consignment"
        );

        ConsignmentResponse created = consignmentService.createConsignment(request);

        ConsignmentResponse updated = consignmentService.updateStatus(
            created.id(),
            ConsignmentRequestStatus.APPROVED,
            "Approved by committee"
        );

        assertEquals(ConsignmentRequestStatus.APPROVED, updated.status());
    }

    @Test
    void testCancelRequestSuccess() {
        CreateConsignmentRequest request = new CreateConsignmentRequest(
            consignorUser.getId(),
            "CONS001",
            ConsignmentRequestStatus.DRAFT,
            "Request consignment"
        );

        ConsignmentResponse created = consignmentService.createConsignment(request);

        ConsignmentResponse cancelled = consignmentService.updateStatus(
            created.id(),
            ConsignmentRequestStatus.CANCELLED,
            "User cancelled"
        );

        assertEquals(ConsignmentRequestStatus.CANCELLED, cancelled.status());
    }
}
