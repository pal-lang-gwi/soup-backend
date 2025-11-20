package com.palangwi.soup.news.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Summary {
    private String shortSummary;
    private String longSummary;

    @CreatedDate
    private LocalDateTime createdDate;

    public Summary(String shortSummary, String longSummary) {
        this.shortSummary = shortSummary;
        this.longSummary = longSummary;
        this.createdDate = LocalDateTime.now();
    }
}