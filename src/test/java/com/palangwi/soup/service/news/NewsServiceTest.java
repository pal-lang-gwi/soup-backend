package com.palangwi.soup.service.news;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.palangwi.soup.IntegrationTestSupport;
import com.palangwi.soup.domain.news.News;
import com.palangwi.soup.dto.news.NewsResult;
import com.palangwi.soup.dto.news.NewsSummary;
import com.palangwi.soup.repository.news.NewsRepository;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
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
        Long keywordId = 1L;
        String keywordName = "인공지능";

        NewsResult mockResult = new NewsResult(
                keywordId,
                keywordName,
                new NewsSummary.Summary("짧은 요약", "긴 요약"),
                List.of(new NewsSummary.Article("제목", "https://link", "요약")),
                123
        );

        given(perplexityAIService.searchAndSummarizeAsync(keywordId, keywordName))
                .willReturn(CompletableFuture.completedFuture(mockResult));

        // when
        newsService.collectAndSaveNews(keywordId);

        // then
        ArgumentCaptor<News> captor = ArgumentCaptor.forClass(News.class);

        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() ->
                        verify(newsRepository).save(captor.capture())
                );

        News savedNews = captor.getValue();
        assertThat(savedNews.getKeywordId()).isEqualTo(keywordId);
        assertThat(savedNews.getKeywordName()).isEqualTo(keywordName);
        assertThat(savedNews.getTokens()).isEqualTo(123);
        assertThat(savedNews.getSummary().getShortSummary()).isEqualTo("짧은 요약");
        assertThat(savedNews.getSummary().getLongSummary()).isEqualTo("긴 요약");
        assertThat(savedNews.getArticles()).hasSize(1);
        assertThat(savedNews.getArticles().getFirst().getTitle()).isEqualTo("제목");
    }
}