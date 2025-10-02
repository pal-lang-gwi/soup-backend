package com.palangwi.soup.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${perplexity.api-key}")
    private String apiKey;

    @Value("${perplexity.base-url}")
    private String baseUrl;

    @Value("${openai.api-key}")
    private String openaiApiKey;

    @Value("${openai.base-url}")
    private String openaiBaseUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient openaiWebClient() {
        return WebClient.builder()
                .baseUrl(openaiBaseUrl)
                .defaultHeader("Authorization", "Bearer " + openaiApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
