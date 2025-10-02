package com.palangwi.soup.service.embedding;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palangwi.soup.IntegrationTestSupport;
import com.palangwi.soup.exception.embedding.EmbeddingGenerationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class EmbeddingServiceTest extends IntegrationTestSupport {

    @Autowired
    private EmbeddingService embeddingService;

    @MockitoBean
    @Qualifier("openaiWebClient")
    private WebClient openaiWebClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGenerateEmbedding_withMockedWebClient() throws Exception {
        // given
        String testText = "테스트 입력";
        String fakeResponse = """
            {
              "data": [
                {
                  "embedding": [0.11, 0.22, 0.33]
                }
              ]
            }
            """;

        // WebClient mock 체인 설정
        WebClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = Mockito.mock(WebClient.RequestBodySpec.class);
        @SuppressWarnings("rawtypes")
        WebClient.RequestHeadersSpec requestHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

        Mockito.when(openaiWebClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.bodyValue(any(Map.class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToMono(eq(String.class)))
                .thenReturn(Mono.just(fakeResponse));

        // 설정값 주입
        ReflectionTestUtils.setField(embeddingService, "model", "text-embedding-ada-002");
        ReflectionTestUtils.setField(embeddingService, "embeddingsPath", "/embeddings");

        // when
        CompletableFuture<float[]> future = embeddingService.generateEmbedding(testText);
        float[] result = future.get();

        // then
        assertThat(result).containsExactly(0.11f, 0.22f, 0.33f);
    }

    @Test
    void testGenerateEmbedding_whenApiError_thenThrowException() {
        // given
        String testText = "테스트 입력";

        WebClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = Mockito.mock(WebClient.RequestBodySpec.class);
        @SuppressWarnings("rawtypes")
        WebClient.RequestHeadersSpec requestHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

        Mockito.when(openaiWebClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.bodyValue(any(Map.class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToMono(eq(String.class)))
                .thenReturn(Mono.error(WebClientResponseException.create(500, "Internal Server Error", null, null, null)));

        // 설정값 주입
        ReflectionTestUtils.setField(embeddingService, "model", "text-embedding-ada-002");
        ReflectionTestUtils.setField(embeddingService, "embeddingsPath", "/embeddings");

        // when & then
        assertThatThrownBy(() -> embeddingService.generateEmbedding(testText).get())
                .isInstanceOf(ExecutionException.class)
                .hasRootCauseInstanceOf(WebClientResponseException.class);
    }

    @Test
    void testGenerateEmbedding_whenTimeout_thenThrowException() {
        // given
        String testText = "테스트 입력";

        WebClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = Mockito.mock(WebClient.RequestBodySpec.class);
        @SuppressWarnings("rawtypes")
        WebClient.RequestHeadersSpec requestHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

        Mockito.when(openaiWebClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.bodyValue(any(Map.class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToMono(eq(String.class)))
                .thenReturn(Mono.delay(Duration.ofSeconds(35)).then(Mono.empty()));

        // 설정값 주입
        ReflectionTestUtils.setField(embeddingService, "model", "text-embedding-ada-002");
        ReflectionTestUtils.setField(embeddingService, "embeddingsPath", "/embeddings");

        // when & then
        assertThatThrownBy(() -> embeddingService.generateEmbedding(testText).get())
                .isInstanceOf(ExecutionException.class)
                .hasRootCauseInstanceOf(java.util.concurrent.TimeoutException.class);
    }

    @Test
    void testGenerateEmbedding_whenInvalidJson_thenThrowEmbeddingGenerationException() {
        // given
        String testText = "테스트 입력";
        String invalidJson = "잘못된 JSON 응답";

        WebClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = Mockito.mock(WebClient.RequestBodySpec.class);
        @SuppressWarnings("rawtypes")
        WebClient.RequestHeadersSpec requestHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

        Mockito.when(openaiWebClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.bodyValue(any(Map.class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToMono(eq(String.class)))
                .thenReturn(Mono.just(invalidJson));

        // 설정값 주입
        ReflectionTestUtils.setField(embeddingService, "model", "text-embedding-ada-002");
        ReflectionTestUtils.setField(embeddingService, "embeddingsPath", "/embeddings");

        // when & then
        assertThatThrownBy(() -> embeddingService.generateEmbedding(testText).get())
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(EmbeddingGenerationException.class);
    }
}