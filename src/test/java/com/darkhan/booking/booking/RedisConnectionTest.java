package com.darkhan.booking.booking;

import com.darkhan.booking.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class RedisConnectionTest {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    void redis_roundTrip_works() {
        redisTemplate.opsForValue().set("ping", "pong");
        String value = redisTemplate.opsForValue().get("ping");
        assertThat(value).isEqualTo("pong");
    }
}
