package com.palangwi.soup.dto.keyword.response;

import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.user.User;

public record RequestKeywordResponseDto(Long userId, String keyword) {
    public static RequestKeywordResponseDto of(User user, Keyword keyword) {
        return new RequestKeywordResponseDto(
                user.getId(),
                keyword.getName()
        );
    }
}
