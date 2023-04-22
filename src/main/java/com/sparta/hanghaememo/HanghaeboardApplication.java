package com.sparta.hanghaememo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@EnableJpaAuditing
@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true)
public class HanghaeboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(HanghaeboardApplication.class, args);
    }

}
