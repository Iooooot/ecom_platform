package com.db.ecom_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EcomPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomPlatformApplication.class, args);
    }

}
