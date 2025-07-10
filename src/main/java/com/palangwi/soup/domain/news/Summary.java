package com.palangwi.soup.domain.news;

import com.palangwi.soup.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Summary extends BaseEntity {
    private String shortSummary;
    private String longSummary;

    public Summary(String shortSummary, String longSummary) {
        this.shortSummary = shortSummary;
        this.longSummary = longSummary;
    }
}