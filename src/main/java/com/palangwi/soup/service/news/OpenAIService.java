package com.palangwi.soup.service.news;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palangwi.soup.dto.news.OpenAIWebSearchRequestDto;
import com.palangwi.soup.dto.news.OpenAIWebSearchResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.palangwi.soup.dto.news.OpenAIWebSearchResponseDto.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${openai.api.url}")
    private String openaiUrl;

    @Value("${openai.api.key}")
    private String openaiKey;

    @Value(("${openai.model}"))
    private String openaiModel;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CompletableFuture<String> searchAndSummarizeAsync(String keyword) {
        CompletableFuture<String> future = new CompletableFuture<>();

        try {
            String prompt = loadPrompt("prompts/news-prompt.txt").replace("{keyword}", keyword);
            Request request = buildOpenAIRequest(prompt);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    log.error("🔴 OpenAI async 호출 실패 - {}", keyword, e);
                    future.completeExceptionally(e);
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try (response) {
                        if (!response.isSuccessful()) {
                            future.completeExceptionally(new RuntimeException("OpenAI 호출 실패: " + response.code()));
                            return;
                        }

                        future.complete(parseOpenAIResponse(response));
                    } catch (Exception ex) {
                        future.completeExceptionally(ex);
                    }
                }
            });

        } catch (Exception e) {
            future.completeExceptionally(e);
        }

        return future;
    }

    private String parseOpenAIResponse(Response response) throws IOException {
        OpenAIWebSearchResponseDto parsed = objectMapper.readValue(response.body().string(), OpenAIWebSearchResponseDto.class);

        return parsed.output().stream()
                .filter(o -> "message".equals(o.type()))
                .map(o -> objectMapper.convertValue(o, MessageOutput.class))
                .flatMap(m -> m.content().stream())
                .filter(c -> "output_text".equals(c.type()))
                .map(Content::text)
                .findFirst()
                .orElse("결과 없음");
    }

    private Request buildOpenAIRequest(String prompt) throws JsonProcessingException {
        OpenAIWebSearchRequestDto requestBody = new OpenAIWebSearchRequestDto(
                openaiModel,
                List.of(new OpenAIWebSearchRequestDto.Tool("web_search_preview")),
                prompt,
                true,
                "auto"
        );

        return new Request.Builder()
                .url(openaiUrl)
                .header("Authorization", "Bearer " + openaiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(
                        objectMapper.writeValueAsString(requestBody),
                        MediaType.parse("application/json")))
                .build();
    }

    private String loadPrompt(String path) {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("프롬프트 파일 로딩 실패: " + path, e);
        }
    }
}
