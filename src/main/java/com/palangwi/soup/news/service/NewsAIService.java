package com.palangwi.soup.news.service;

import com.palangwi.soup.news.dto.NewsResult;

import java.util.concurrent.CompletableFuture;

public interface NewsAIService {
    CompletableFuture<NewsResult> searchAndSummarizeAsync(Long keywordId, String keyword);
}
