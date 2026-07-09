package com.goMaddy.multithreaded_http_fileserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.thread-pool")
@Component
public class ThreadPoolProperties {
private int size;
public int getSize() {
        return size;}
public void setSize(int size) {
        this.size = size; }
}
