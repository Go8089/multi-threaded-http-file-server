package com.goMaddy.multithreaded_http_fileserver.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.rate-limit")
@Component
public class RateLimitProperties {

    private int capacity = 50;
    private int refillTokens = 50;
    private int refillMinutes = 1;

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getRefillTokens() {
        return refillTokens;
    }

    public void setRefillTokens(int refillTokens) {
        this.refillTokens = refillTokens;
    }

    public int getRefillMinutes() {
        return refillMinutes;
    }

    public void setRefillMinutes(int refillMinutes) {
        this.refillMinutes = refillMinutes;
    }
}