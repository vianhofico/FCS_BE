package com.fcs.be.modules.analytics.dto.response;

import com.fcs.be.common.enums.ConsignmentRequestStatus;
import java.util.Map;

public record ConsignmentAnalyticsResponse(
    Map<ConsignmentRequestStatus, Long> totalByStatus,
    Double conversionRate
) {}
