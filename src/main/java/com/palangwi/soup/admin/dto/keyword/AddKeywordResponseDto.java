package com.palangwi.soup.admin.dto.keyword;

public record AddKeywordResponseDto(String keyword) {
    public static AddKeywordResponseDto of (String keyword) {
        return new AddKeywordResponseDto(keyword);
    }
}
