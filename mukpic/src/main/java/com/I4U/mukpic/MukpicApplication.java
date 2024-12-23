package com.I4U.mukpic;

import com.I4U.mukpic.utils.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MukpicApplication {

	public static void main(String[] args) {

		EnvConfig.loadEnv();

		SpringApplication.run(MukpicApplication.class, args);
	}

}
