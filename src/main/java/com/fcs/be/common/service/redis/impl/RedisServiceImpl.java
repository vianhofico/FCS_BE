package com.fcs.be.common.service.redis.impl;

import com.fcs.be.common.service.redis.interfaces.RedisService;
import java.time.Duration;
import java.util.Optional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, String value, Duration ttl) {
        Assert.hasText(key, "key must not be blank");
        Assert.notNull(value, "value must not be null");
        Assert.notNull(ttl, "ttl must not be null");
        Assert.isTrue(!ttl.isNegative() && !ttl.isZero(), "ttl must be greater than zero");
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    @Override
    public Optional<String> get(String key) {
        Assert.hasText(key, "key must not be blank");
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public void delete(String key) {
        Assert.hasText(key, "key must not be blank");
        redisTemplate.delete(key);
    }

    @Override
    public long publish(String channel, String message) {
        Assert.hasText(channel, "channel must not be blank");
        Assert.notNull(message, "message must not be null");
        Long receivers = redisTemplate.convertAndSend(channel, message);
        return receivers == null ? 0L : receivers;
    }
}
