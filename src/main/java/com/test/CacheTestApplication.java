package com.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@SpringBootApplication
public class CacheTestApplication extends CachingConfigurerSupport {

	public static void main(String[] args) {
		SpringApplication.run(CacheTestApplication.class, args);
	}
	
	 
}
