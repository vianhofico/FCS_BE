package com.fcs.be.common.service.redis.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fcs.be.common.service.redis.interfaces.RedisService;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class RedisServiceImplTest {

    private final StringRedisTemplate redisTemplate = Mockito.mock(StringRedisTemplate.class);
    private final ValueOperations<String, String> valueOperations = Mockito.mock(ValueOperations.class);
    private final RedisService service = new RedisServiceImpl(redisTemplate);

    @Test
    void shouldSetValueWithTtl() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        service.set("k1", "v1", Duration.ofMinutes(5));

        verify(valueOperations).set("k1", "v1", Duration.ofMinutes(5));
    }

    @Test
    void shouldReturnOptionalValueWhenKeyExists() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("k1")).thenReturn("v1");

        Optional<String> result = service.get("k1");

        assertEquals(Optional.of("v1"), result);
    }

    @Test
    void shouldPublishMessageToChannel() {
        when(redisTemplate.convertAndSend("channel-1", "message-1")).thenReturn(2L);

        long receivers = service.publish("channel-1", "message-1");

        assertEquals(2L, receivers);
    }

    @Test
    void shouldThrowWhenTtlIsZero() {
        assertThrows(
            IllegalArgumentException.class,
            () -> service.set("k1", "v1", Duration.ZERO)
        );
    }
}
