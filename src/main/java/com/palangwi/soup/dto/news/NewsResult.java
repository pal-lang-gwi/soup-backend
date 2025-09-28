package com.palangwi.soup.dto.news;

import com.palangwi.soup.domain.news.Article;
import com.palangwi.soup.domain.news.News;
import com.palangwi.soup.domain.news.Summary;

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