package com.hanami.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CmsApplication {

	public static void main(String[] args) {
		Runnable run = () -> System.out.println("running");
		
		(new Thread(run)).start();
		
		SpringApplication.run(CmsApplication.class, args);
	}
}
