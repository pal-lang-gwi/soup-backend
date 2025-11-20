package com.palangwi.soup.keyword.dto.response;

import com.palangwi.soup.subscription.domain.UserKeyword;

public record KeywordUnsubscribeResponseDto(Long userId, String keywordName) {
    public static KeywordUnsubscribeResponseDto of(UserKeyword userKeyword) {
        return new KeywordUnsubscribeResponseDto(userKeyword.getUser().getId(), userKeyword.getKeyword().getName());
    }
}
