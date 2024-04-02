package com.fea.foreign.exchange.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class ForeignExchangeAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForeignExchangeAppApplication.class, args);
	}

}
