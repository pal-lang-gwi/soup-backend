package com.palangwi.soup.service.news;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palangwi.soup.IntegrationTestSupport;
import com.palangwi.soup.dto.news.NewsResult;
import java.util.concurrent.CompletableFuture;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

class PerplexityAIServiceImplTest extends IntegrationTestSupport {

    private PerplexityAIServiceImpl perplexityAIService;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        perplexityAIService = new PerplexityAIServiceImpl(webClient, new ObjectMapper());

        ReflectionTestUtils.setField(perplexityAIService, "model", "sonar");
        ReflectionTestUtils.setField(perplexityAIService, "completionsPath", "/v1/completions");
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void searchAndSummarizeAsync_returnsNewsResult() {
        // given
        String mockJson = """
                {
                  "usage": {
                    "total_tokens": 321
                  },
                  "choices": [
                    {
                      "message": {
                        "content": "```json\\n{ \\"keyword\\": \\"인공지능\\", \\"summary\\": { \\"short_summary\\": \\"짧은 요약\\", \\"long_summary\\": \\"긴 요약\\" }, \\"articles\\": [ { \\"title\\": \\"제목\\", \\"link\\": \\"https://link\\", \\"summary\\": \\"기사요약\\" } ] }\\n```"
                      }
                    }
                  ]
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockJson)
                .addHeader("Content-Type", "application/json"));

        // when
        CompletableFuture<NewsResult> future = perplexityAIService.searchAndSummarizeAsync("인공지능");
        NewsResult result = future.join();

        // then
        assertThat(result.keyword()).isEqualTo("인공지능");
        assertThat(result.summary().short_summary()).isEqualTo("짧은 요약");
        assertThat(result.articles()).hasSize(1);
        assertThat(result.tokens()).isEqualTo(321);
    }
}