package com.palangwi.soup.news.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palangwi.soup.news.dto.NewsResult;
import com.palangwi.soup.news.dto.NewsSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerplexityAIServiceImpl implements NewsAIService {

    private static final DateTimeFormatter TODAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
    private static final String PROMPT_PATH = "prompts/news-prompt.txt";

    @Value("${perplexity.model}")
    private String model;

    @Value("${perplexity.completions-path}")
    private String completionsPath;

    @Value("${news.prompt-path:}")
    private String promptPath;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public CompletableFuture<NewsResult> searchAndSummarizeAsync(Long keywordId, String keywordName) {
        String today = getTodayString();
        String prompt = loadPrompt()
                .replace("{keyword}", keywordName)
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
                .map(content -> parseToNewsResult(content, keywordId, keywordName))
                .toFuture();
    }

    private NewsResult parseToNewsResult(String content, Long keywordId, String keywordName) {
        try {
            log.debug("🔎 Perplexity 원시 응답:\n{}", content);

            int tokens = objectMapper.readTree(content)
                    .path("usage")
                    .path("total_tokens")
                    .asInt(-1);

            String rawJson = objectMapper.readTree(content)
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText()
                    .trim();

            String cleanedJson = rawJson
                    .replaceAll("^```json\\s*", "")
                    .replaceAll("^```\\s*", "")
                    .replaceAll("```\\s*$", "")
                    .trim();

            log.debug("📝 클린된 요약 JSON:\n{}", cleanedJson);

            NewsSummary summary = objectMapper.readValue(cleanedJson, NewsSummary.class);

            return new NewsResult(
                    keywordId,
                    keywordName,
                    summary.summary(),
                    summary.articles(),
                    tokens
            );

        } catch (Exception e) {
            log.error("❌ JSON 파싱 실패", e);
            throw new RuntimeException("❌ Perplexity 응답 처리 중 오류 발생", e);
        }
    }

    private static String getTodayString() {
        return LocalDate.now().format(TODAY_FORMATTER);
    }

    private String loadPrompt() {
        if (promptPath != null && !promptPath.isBlank()) {
            try {
                return Files.readString(Path.of(promptPath), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException("프롬프트 파일 로딩 실패: " + promptPath, e);
            }
        }

        try (InputStream is = new ClassPathResource(PROMPT_PATH).getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("프롬프트 파일 로딩 실패: " + PROMPT_PATH, e);
        }
    }
}
