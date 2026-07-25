package com.darkhan.booking.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.data.redis.autoconfigure.DataRedisConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Bean
    public RedissonClient redissonClient(DataRedisConnectionDetails details) {
        String host = details.getStandalone().getHost();
        int port = details.getStandalone().getPort();

        String redisUrl = "redis://" + host + ":" + port;
        Config config = new Config();
        config.useSingleServer().setAddress(redisUrl);

        return Redisson.create(config);
    }
}
