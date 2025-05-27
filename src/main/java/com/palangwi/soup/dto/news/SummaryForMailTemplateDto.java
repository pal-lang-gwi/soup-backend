package com.palangwi.soup.dto.news;

import lombok.Getter;

@Getter
public class SummaryForMailTemplateDto {
    private final String keyword;
    private final String shortSummary;

    public SummaryForMailTemplateDto(String keyword, String shortSummary) {
        this.keyword = keyword;
        this.shortSummary = shortSummary;
    }
}
