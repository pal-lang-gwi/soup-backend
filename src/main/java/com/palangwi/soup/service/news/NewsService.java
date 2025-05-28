package com.palangwi.soup.service.news;

import com.palangwi.soup.domain.news.News;
import com.palangwi.soup.repository.news.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final OpenAIService openAIService;

    public void collectAndSaveNews(String keyword) {
        openAIService.searchAndSummarizeAsync(keyword)
                .thenAccept(result -> {
                    try {
                        News news = result.toNews();
                        newsRepository.save(news);
                    } catch (Exception e) {
                        log.error("❌ 뉴스 파싱 실패 - {}", keyword, e);
                    }
                })
                .exceptionally(ex -> {
                    log.error("❌ OpenAI 응답 실패 - {}", keyword, ex);
                    return null;
                });
    }
}