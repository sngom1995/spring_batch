package com.samba.springboot;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableBatchProcessing
@ComponentScan({"com.samba.config","com.samba.service", "com.samba.listener","com.samba.writer","com.samba.reader"
		,"com.samba.processor", "com.samba.controller"})
@EnableAsync
@EnableScheduling
public class SpringBatchAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchAppApplication.class, args);
	}

}
