package com.reto.ms_persona.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${ms.bootcamp.url:http://localhost:8082}")
    private String bootcampBaseUrl;

    @Bean(name = "bootcampServiceWebClient")
    public WebClient bootcampServiceWebClient() {
        return WebClient.builder()
                .baseUrl(bootcampBaseUrl)
                .build();
    }
}

