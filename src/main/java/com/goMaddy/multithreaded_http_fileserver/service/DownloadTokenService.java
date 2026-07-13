package com.goMaddy.multithreaded_http_fileserver.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class DownloadTokenService {

    private static final Duration TOKEN_TTL = Duration.ofMinutes(5);

    private final StringRedisTemplate redisTemplate;

    public DownloadTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateToken(UUID fileId, Long userId) {

        String token = UUID.randomUUID().toString();

        String key = "download:" + token;

        String value = fileId + ":" + userId;

        redisTemplate.opsForValue()
                .set(key, value, TOKEN_TTL);

        return token;
    }

    public String getTokenData(String token) {

        return redisTemplate.opsForValue()
                .get("download:" + token);
    }

    public void deleteToken(String token) {

        redisTemplate.delete("download:" + token);
    }
}
