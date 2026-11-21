package com.wscicloud.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "com.wscicloud.service.immobilier.domain.repository")
@EntityScan(basePackages = "com.wscicloud.service.immobilier.domain.entities")
public class ImmobilierStarter {

    public static void main(String[] args) {
        SpringApplication.run(ImmobilierStarter.class, args);
    }
}
