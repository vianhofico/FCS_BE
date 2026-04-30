package com.fcs.be.modules.audit.service.impl;

import com.fcs.be.common.enums.ActivityAction;
import com.fcs.be.common.enums.UserStatus;
import com.fcs.be.modules.audit.dto.response.ActivityLogResponse;
import com.fcs.be.modules.audit.repository.ActivityLogRepository;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActivityLogServiceImplTest {

    @Autowired
    private ActivityLogServiceImpl activityLogService;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        activityLogRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
            .username("audituser")
            .email("audit@example.com")
            .passwordHash("hashed")
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(testUser);
    }

    @Test
    void testLogActivitySuccess() {
        UUID entityId = UUID.randomUUID();

        activityLogService.log(
            testUser.getId(),
            ActivityAction.CREATE,
            "Product",
            entityId,
            null,
            "New product created",
            "127.0.0.1",
            "Mozilla/5.0"
        );

        List<ActivityLogResponse> logs = activityLogService.getActivityLogs();

        assertEquals(1, logs.size());
        assertEquals(ActivityAction.CREATE, logs.get(0).action());
    }

    @Test
    void testLogMultipleActivities() {
        UUID entityId1 = UUID.randomUUID();
        UUID entityId2 = UUID.randomUUID();

        activityLogService.log(
            testUser.getId(),
            ActivityAction.CREATE,
            "Product",
            entityId1,
            null,
            "Product created",
            "127.0.0.1",
            "Mozilla/5.0"
        );

        activityLogService.log(
            testUser.getId(),
            ActivityAction.UPDATE,
            "Order",
            entityId2,
            "status=PENDING",
            "status=COMPLETED",
            "127.0.0.1",
            "Mozilla/5.0"
        );

        List<ActivityLogResponse> logs = activityLogService.getActivityLogs();

        assertEquals(2, logs.size());
    }

    @Test
    void testGetActivityLogsSuccess() {
        UUID entityId = UUID.randomUUID();

        activityLogService.log(
            testUser.getId(),
            ActivityAction.CREATE,
            "Product",
            entityId,
            null,
            "Product created",
            "127.0.0.1",
            "Mozilla/5.0"
        );

        List<ActivityLogResponse> logs = activityLogService.getActivityLogs();

        assertNotNull(logs);
        assertEquals(1, logs.size());
    }

    @Test
    void testLogDeleteAction() {
        UUID entityId = UUID.randomUUID();

        activityLogService.log(
            testUser.getId(),
            ActivityAction.DELETE,
            "Product",
            entityId,
            "all data deleted",
            null,
            "127.0.0.1",
            "Mozilla/5.0"
        );

        List<ActivityLogResponse> logs = activityLogService.getActivityLogs();

        assertEquals(1, logs.size());
        assertEquals(ActivityAction.DELETE, logs.get(0).action());
    }

    @Test
    void testGetActivityLogsEmpty() {
        List<ActivityLogResponse> logs = activityLogService.getActivityLogs();

        assertTrue(logs.isEmpty());
    }
}
