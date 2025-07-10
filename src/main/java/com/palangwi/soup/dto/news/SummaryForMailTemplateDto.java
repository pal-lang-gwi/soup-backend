package com.palangwi.soup.dto.news;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class SummaryForMailTemplateDto {
    private final String keyword;
    private final String shortSummary;
    private final LocalDate createdDay;

    public SummaryForMailTemplateDto(String keyword, String shortSummary, LocalDate createdDay) {
        this.keyword = keyword;
        this.shortSummary = shortSummary;
        this.createdDay = createdDay;
    }
}
