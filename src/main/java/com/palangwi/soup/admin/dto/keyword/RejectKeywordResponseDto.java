package com.palangwi.soup.admin.dto.keyword;

import com.palangwi.soup.keyword.domain.Keyword;

public record RejectKeywordResponseDto(String keyword, String rejectReason) {
    public static RejectKeywordResponseDto of(Keyword keyword, String rejectReason) {
        return new RejectKeywordResponseDto(keyword.getName(), rejectReason);
    }
}
