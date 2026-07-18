package com.goMaddy.multithreaded_http_fileserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.goMaddy.multithreaded_http_fileserver.config.AwsS3Properties;

//@EnableConfigurationProperties({JwtProperties.class, StorageProperties.class, ThreadPoolProperties.class})
@EnableConfigurationProperties(AwsS3Properties.class)
@SpringBootApplication
public class MultithreadedHttpFileserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultithreadedHttpFileserverApplication.class, args);
	}

}
