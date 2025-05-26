package com.palangwi.soup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SoupApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoupApplication.class, args);
    }

}
