package com.wscicloud.service.config;

import java.util.Properties;

import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableFeignClients
public class SpringConfig {
    @Bean
    BuildProperties buildProperties() {
        return new BuildProperties(new Properties());
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }
}
