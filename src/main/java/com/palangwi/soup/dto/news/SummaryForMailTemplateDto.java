package com.palangwi.soup.dto.news;

import java.time.LocalDate;
import lombok.Getter;

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
