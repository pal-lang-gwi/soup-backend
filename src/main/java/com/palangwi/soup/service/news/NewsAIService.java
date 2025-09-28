package com.palangwi.soup.service.news;

import com.palangwi.soup.dto.news.NewsResult;
import java.util.concurrent.CompletableFuture;

public interface NewsAIService {
    CompletableFuture<NewsResult> searchAndSummarizeAsync(Long keywordId, String keyword);
}
