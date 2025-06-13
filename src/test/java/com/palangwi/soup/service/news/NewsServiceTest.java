package com.palangwi.soup.service.news;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.palangwi.soup.IntegrationTestSupport;
import com.palangwi.soup.domain.news.Article;
import com.palangwi.soup.domain.news.News;
import com.palangwi.soup.dto.news.ArticleDto;
import com.palangwi.soup.dto.news.NewsResult;
import com.palangwi.soup.dto.news.NewsSummary;
import com.palangwi.soup.repository.news.NewsRepository;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class NewsServiceTest extends IntegrationTestSupport {

    @Autowired
    private NewsService newsService;

    @MockitoBean
    private NewsRepository newsRepository;

    @MockitoBean
    private PerplexityAIServiceImpl perplexityAIService;

    @Test
    @DisplayName("AI 뉴스 요약 결과를 저장한다.")
    void collectAndSaveNews() {
        // given
        String keyword = "인공지능";
        NewsResult mockResult = new NewsResult(
                keyword,
                new NewsSummary.Summary("짧은 요약", "긴 요약"),
                List.of(new NewsSummary.Article("제목", "https://link", "요약")),
                123
        );

        given(perplexityAIService.searchAndSummarizeAsync(keyword))
                .willReturn(CompletableFuture.completedFuture(mockResult));

        // when
        newsService.collectAndSaveNews(keyword);

        // then
        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() ->
                        verify(newsRepository).save(any(News.class))
                );
    }
}