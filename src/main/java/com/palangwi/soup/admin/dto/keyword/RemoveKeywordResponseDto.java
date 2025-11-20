package com.palangwi.soup.admin.dto.keyword;

import com.palangwi.soup.keyword.domain.Keyword;

public record RemoveKeywordResponseDto(String keyword, String removeReason) {
    public static RemoveKeywordResponseDto of(Keyword keyword, String removeReason) {
        return new RemoveKeywordResponseDto(keyword.getName(), removeReason);
    }
}
