package com.palangwi.soup.news.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SummaryForMailTemplateDto {
    private final Long keywordId;
    private final String keywordName;
    private final String shortSummary;
    private final LocalDate createdDate;

    public SummaryForMailTemplateDto(Long keywordId, String keywordName, String shortSummary, LocalDate createdDate) {
        this.keywordId = keywordId;
        this.keywordName = keywordName;
        this.shortSummary = shortSummary;
        this.createdDate = createdDate;
    }
}
