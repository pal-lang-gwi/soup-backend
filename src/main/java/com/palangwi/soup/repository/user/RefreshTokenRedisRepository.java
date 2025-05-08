package com.palangwi.soup.repository.user;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RefreshTokenRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String PREFIX = "refresh_token:";

    public void save(String refreshToken, Long userId, long ttlSeconds) {
        redisTemplate.opsForValue()
                .set(getKey(refreshToken), userId.toString(), ttlSeconds, TimeUnit.SECONDS);
    }

    public String findUserIdByRefreshToken(String refreshToken) {
        return redisTemplate.opsForValue().get(PREFIX + refreshToken);
    }

    public void delete(String refreshToken) {
        redisTemplate.delete(PREFIX + refreshToken);
    }

    private String getKey(String refreshToken) {
        return PREFIX + refreshToken;
    }
}
