package com.palangwi.soup.dto.admin.keyword;

public record ApproveKeywordResponseDto(String keyword, int requestedUser) {
    public static ApproveKeywordResponseDto of (String keyword, int requestedUser) {
        return new ApproveKeywordResponseDto(keyword, requestedUser);
    }
}
