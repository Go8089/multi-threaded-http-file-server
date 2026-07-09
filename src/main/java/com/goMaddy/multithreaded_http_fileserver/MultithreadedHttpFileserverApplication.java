package com.goMaddy.multithreaded_http_fileserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableConfigurationProperties({JwtProperties.class, StorageProperties.class, ThreadPoolProperties.class})
@SpringBootApplication
public class MultithreadedHttpFileserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultithreadedHttpFileserverApplication.class, args);
	}

}
