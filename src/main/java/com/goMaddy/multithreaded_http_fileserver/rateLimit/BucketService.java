package com.goMaddy.multithreaded_http_fileserver.rateLimit;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.goMaddy.multithreaded_http_fileserver.config.properties.RateLimitProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

@Service
public class BucketService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private final RateLimitProperties properties;

    public BucketService(RateLimitProperties properties) {
        this.properties = properties;
    }

    public Bucket resolveBucket(String key) {
        return buckets.computeIfAbsent(key, k -> newBucket());
    }

    private Bucket newBucket() {

        Bandwidth limit = Bandwidth.builder()
                .capacity(properties.getCapacity())
                .refillGreedy(
                        properties.getRefillTokens(),
                        Duration.ofMinutes(properties.getRefillMinutes())
                )
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
