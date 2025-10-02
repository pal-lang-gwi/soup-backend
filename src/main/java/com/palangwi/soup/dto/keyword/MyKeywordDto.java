package com.palangwi.soup.dto.keyword;

import com.palangwi.soup.domain.userkeyword.UserKeyword;

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