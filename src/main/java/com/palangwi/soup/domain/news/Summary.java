package com.palangwi.soup.domain.news;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Summary {
    private String shortSummary;
    private String longSummary;

    public Summary(String shortSummary, String longSummary) {
        this.shortSummary = shortSummary;
        this.longSummary = longSummary;
    }
}