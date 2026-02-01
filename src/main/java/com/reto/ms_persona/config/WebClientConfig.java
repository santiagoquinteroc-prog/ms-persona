package com.reto.ms_persona.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${ms.bootcamp.url:http://localhost:8082}")
    private String bootcampBaseUrl;

    @Value("${ms.reporte.url:http://localhost:8084}")
    private String reporteBaseUrl;

    @Bean(name = "bootcampServiceWebClient")
    public WebClient bootcampServiceWebClient() {
        return WebClient.builder()
                .baseUrl(bootcampBaseUrl)
                .build();
    }

    @Bean(name = "reporteServiceWebClient")
    public WebClient reporteServiceWebClient() {
        return WebClient.builder()
                .baseUrl(reporteBaseUrl)
                .build();
    }
}

