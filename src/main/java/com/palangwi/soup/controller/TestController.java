package com.palangwi.soup.controller;

import static com.palangwi.soup.utils.ApiUtils.success;

import com.palangwi.soup.dto.news.NewsResult;
import com.palangwi.soup.service.news.NewsAIService;
import com.palangwi.soup.utils.ApiUtils.ApiResult;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

    private static NewsAIService newsAIService;

    @GetMapping
    public ApiResult<CompletableFuture<NewsResult>> test() {
        return success(newsAIService.searchAndSummarizeAsync("AI"));
    }
}
