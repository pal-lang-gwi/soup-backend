package com.palangwi.soup.news.dto;

import com.palangwi.soup.news.domain.Article;
import com.palangwi.soup.news.domain.News;
import com.palangwi.soup.news.domain.Summary;

import java.util.List;

public record NewsResult(
        Long keywordId,
        String keywordName,
        NewsSummary.Summary summary,
        List<NewsSummary.Article> articles,
        int tokens
) {
    public News toNews() {
        return new News(
                keywordId,
                keywordName,
                new Summary(summary.short_summary(), summary.long_summary()),
                articles.stream()
                        .map(a -> new Article(a.title(), a.link(), a.summary()))
                        .toList(),
                tokens
        );
    }
}