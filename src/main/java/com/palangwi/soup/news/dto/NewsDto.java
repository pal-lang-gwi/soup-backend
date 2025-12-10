package com.palangwi.soup.news.dto;

import com.palangwi.soup.news.domain.News;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public record NewsDto(Long keywordId,
                      String keywordName,
                      String longSummary,
                      List<ArticleDto> articles,
                      List<String> relatedKeywords,
                      LocalDateTime createdDate) {

    public static NewsDto from(News news) {
        return new NewsDto(news.getKeywordId(),
                news.getKeywordName(),
                news.getSummary().getLongSummary(),
                news.getArticles().stream()
                        .map(article -> new ArticleDto(
                                article.getTitle(),
                                article.getLink(),
                                article.getSummary()
                        ))
                        .toList(),
                news.getRelated_keyword(),
                news.getCreatedDate());
    }
}
