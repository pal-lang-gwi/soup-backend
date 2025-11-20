package com.palangwi.soup.keyword.dto;

import com.palangwi.soup.subscription.domain.UserKeyword;

import java.time.LocalDateTime;

public record MyKeywordDto(Long subscriptionId,
                           KeywordInfo keywordInfo) {

    public static MyKeywordDto of(UserKeyword userKeyword) {
        return new MyKeywordDto(
                userKeyword.getId(),
                new KeywordInfo(
                        userKeyword.getKeyword().getId(),
                        userKeyword.getKeyword().getName(),
                        userKeyword.getLastModifiedDate()
                )
        );
    }

    private record KeywordInfo(Long keywordId,
                              String keyword,
                              LocalDateTime registeredAt) {}
}