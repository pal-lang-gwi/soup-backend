package com.palangwi.soup.dto.news;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class SummaryForMailTemplateDto {
    private final String keyword;
    private final String shortSummary;
    private final LocalDate createdDate;

    public SummaryForMailTemplateDto(String keyword, String shortSummary, LocalDate createdDate) {
        this.keyword = keyword;
        this.shortSummary = shortSummary;
        this.createdDate = createdDate;
    }
}
