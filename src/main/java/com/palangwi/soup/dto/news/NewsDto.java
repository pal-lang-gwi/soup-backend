package com.palangwi.soup.dto.news;

import com.palangwi.soup.domain.news.News;
import java.time.LocalDateTime;
import java.util.List;

public record NewsDto(String keyword, String longSummary, List<ArticleDto> articles, LocalDateTime createdDate) {
    public static NewsDto from(News news) {
        return new NewsDto(news.getKeyword(),
                news.getSummary().getLongSummary(),
                news.getArticles().stream()
                        .map(article -> new ArticleDto(
                                article.getTitle(),
                                article.getLink(),
                                article.getSummary()
                        ))
                        .toList(),
                news.getCreatedDate());
    }
}
