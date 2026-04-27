package com.fcs.be.modules.health.service.implements;

import com.fcs.be.modules.health.service.interfaces.HealthService;
import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class HealthServiceImpl implements HealthService {

    @Override
    public Map<String, Object> healthPayload() {
        return Map.of(
            "status", "UP",
            "service", "FCS_BE",
            "timestamp", Instant.now().toString()
        );
    }
}
