package com.palangwi.soup.dto.news;

import java.util.List;

public record NewsSummary(
        String keyword,
        Summary summary,
        List<Article> articles
) {
    public record Summary(
            String short_summary,
            String long_summary
    ) {}

    public record Article(
            String title,
            String link,
            String summary
    ) {}
}