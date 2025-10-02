package com.palangwi.soup.service.embedding;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palangwi.soup.exception.embedding.EmbeddingGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class EmbeddingService {

    private final WebClient openaiWebClient;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.model}")
    private String model;

    @Value("${openai.api.embeddings-path}")
    private String embeddingsPath;

    public EmbeddingService(@Qualifier("openaiWebClient") WebClient openaiWebClient, ObjectMapper objectMapper) {
        this.openaiWebClient = openaiWebClient;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<float[]> generateEmbedding(String text) {
        log.info("🔮 임베딩 생성 시작: {}", text);

        return openaiWebClient.post()
                .uri(embeddingsPath)
                .bodyValue(Map.of(
                        "model", model,
                        "input", text,
                        "encoding_format", "float"))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .retry(3)
                .map(this::parseEmbeddingResponse)
                .toFuture();
    }

    private float[] parseEmbeddingResponse(String response) {
        try {
            log.debug("📝 OpenAI 임베딩 응답: {}", response);

            var jsonNode = objectMapper.readTree(response);
            var embeddingArray = jsonNode.path("data").get(0).path("embedding");

            float[] embedding = new float[embeddingArray.size()];
            for (int i = 0; i < embeddingArray.size(); i++) {
                embedding[i] = embeddingArray.get(i).floatValue();
            }

            log.info("✅ 임베딩 생성 완료: {}차원", embedding.length);
            return embedding;

        } catch (Exception e) {
            log.error("❌ 임베딩 파싱 실패", e);
            throw new EmbeddingGenerationException("임베딩 생성 실패: " + e.getMessage(), e);
        }
    }
}
