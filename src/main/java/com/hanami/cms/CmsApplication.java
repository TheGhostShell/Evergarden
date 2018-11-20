package com.hanami.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.function.Function;

@SpringBootApplication
public class CmsApplication {

	public static void main(String[] args) {
		Runnable run = () -> System.out.println("running");
		
		Function<String, String> hello = (String name) -> {
			return "Hello " + name;
		};
		
		System.out.println(hello("john"));
		
		(new Thread(run)).start();
		
		SpringApplication.run(CmsApplication.class, args);
	}
}
