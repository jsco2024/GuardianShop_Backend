package com.ms_security.ms_security;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableEncryptableProperties
@SpringBootApplication
@EnableScheduling
public class MsSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsSecurityApplication.class, args);
	}

}
