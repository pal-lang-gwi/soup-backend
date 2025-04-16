package com.palangwi.soup;

import org.springframework.boot.SpringApplication;

public class TestSoupApplication {

    public static void main(String[] args) {
        SpringApplication.from(SoupApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
