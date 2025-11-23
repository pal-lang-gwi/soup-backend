package com.palangwi.soup.admin.dto.keyword;

public record ApproveKeywordResponseDto(String keyword, int requestedUserCnt) {
    public static ApproveKeywordResponseDto of (String keyword, int requestedUserCnt) {
        return new ApproveKeywordResponseDto(keyword, requestedUserCnt);
    }
}
