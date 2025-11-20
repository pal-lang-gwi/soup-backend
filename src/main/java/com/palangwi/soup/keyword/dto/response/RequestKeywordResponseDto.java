package com.palangwi.soup.keyword.dto.response;

import com.palangwi.soup.keyword.domain.Keyword;
import com.palangwi.soup.user.domain.User;

public record RequestKeywordResponseDto(Long userId, String keyword) {
    public static RequestKeywordResponseDto of(User user, Keyword keyword) {
        return new RequestKeywordResponseDto(
                user.getId(),
                keyword.getName()
        );
    }
}
