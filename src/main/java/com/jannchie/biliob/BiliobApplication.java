package com.jannchie.biliob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author jannchie
 */
@EnableScheduling
@SpringBootApplication
public class BiliobApplication {

	public static void main(String[] args) {
		SpringApplication.run(BiliobApplication.class, args);
	}
}
