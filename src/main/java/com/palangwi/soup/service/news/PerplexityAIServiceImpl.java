package com.palangwi.soup.service.news;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palangwi.soup.dto.news.NewsResult;
import com.palangwi.soup.dto.news.NewsSummary;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerplexityAIServiceImpl implements NewsAIService{

    @Value("${perplexity.model}")
    private String model;

    @Value("${perplexity.completions-path}")
    private String completionsPath;

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CompletableFuture<NewsResult> searchAndSummarizeAsync(String keyword) {
            String today = getTodayString();
            String prompt = loadPrompt()
                    .replace("{keyword}", keyword)
                    .replace("{today}", today);

        return webClient.post()
                .uri(completionsPath)
                .bodyValue(Map.of(
                        "model", model,
                        "messages", List.of(
                                Map.of("role", "user", "content", prompt)
                        )
                ))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .map(this::parseToNewsResult)
                .toFuture();
    }

    private NewsResult parseToNewsResult(String content) {
        try {
            log.warn("🔎 Perplexity 원시 응답:\n{}", content);

            int tokens = objectMapper.readTree(content)
                    .path("usage")
                    .path("total_tokens")
                    .asInt(-1);

            // message.content 파싱
            String rawJson = objectMapper.readTree(content)
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            String cleanedJson = rawJson
                    .replaceAll("^```json\\s*", "")
                    .replaceAll("```\\s*$", "")
                    .trim();

            log.warn("📝 클린된 요약 JSON:\n{}", cleanedJson);

            NewsSummary summary = objectMapper.readValue(cleanedJson, NewsSummary.class);
            return new NewsResult(summary.keyword(), summary.summary(), summary.articles(), tokens); // tokens는 미지원 시 -1
        } catch (IOException e) {
            throw new RuntimeException("응답 파싱 실패", e);
        }
    }

    private static String getTodayString() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
        return today.format(formatter);
    }

    private String loadPrompt() {
        try (InputStream is = new ClassPathResource("prompts/news-prompt.txt").getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("프롬프트 파일 로딩 실패: " + "prompts/news-prompt.txt", e);
        }
    }
}
