package com.palangwi.soup.dto.admin.keyword;

import com.palangwi.soup.domain.keyword.Keyword;

public record RemoveKeywordResponseDto(String keyword, String removeReason) {
    public static RemoveKeywordResponseDto of(Keyword keyword, String removeReason) {
        return new RemoveKeywordResponseDto(keyword.getName(), removeReason);
    }
}
