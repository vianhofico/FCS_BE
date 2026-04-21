package com.fcs.be.modules.health.service;

import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    public Map<String, Object> healthPayload() {
        return Map.of(
            "status", "UP",
            "service", "FCS_BE",
            "timestamp", Instant.now().toString()
        );
    }
}
