package com.palangwi.soup.news.dto;

import com.palangwi.soup.news.domain.News;

import java.time.LocalDate;

public record NewsForMailDto(
        Long keywordId,
        String keywordName,
        String shortSummary,
        LocalDate createdDate
) {
    public static NewsForMailDto from(News news) {
        return new NewsForMailDto(
                news.getKeywordId(),
                news.getKeywordName(),
                news.getSummary().getShortSummary(),
                news.getSummary().getCreatedDate().toLocalDate()
        );
    }
}
