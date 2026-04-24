package com.fcs.be.common.service.redis.interfaces;

import java.time.Duration;
import java.util.Optional;

public interface RedisService {

    void set(String key, String value, Duration ttl);

    Optional<String> get(String key);

    void delete(String key);

    long publish(String channel, String message);
}
