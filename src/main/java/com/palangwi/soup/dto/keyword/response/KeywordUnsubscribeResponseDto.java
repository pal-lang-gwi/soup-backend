package com.palangwi.soup.dto.keyword.response;

import com.palangwi.soup.domain.userkeyword.UserKeyword;

public record KeywordUnsubscribeResponseDto(Long userId, String unsubscribedKeyword) {
    public static KeywordUnsubscribeResponseDto of(UserKeyword userKeyword) {
        return new KeywordUnsubscribeResponseDto(userKeyword.getUser().getId(), userKeyword.getKeyword().getName());
    }
}
