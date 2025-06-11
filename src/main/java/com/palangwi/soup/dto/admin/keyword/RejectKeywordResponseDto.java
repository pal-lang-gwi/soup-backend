package com.palangwi.soup.dto.admin.keyword;

import com.palangwi.soup.domain.keyword.Keyword;

public record RejectKeywordResponseDto(String keyword, String rejectReason) {
    public static RejectKeywordResponseDto of(Keyword keyword, String rejectReason) {
        return new RejectKeywordResponseDto(keyword.getName(), rejectReason);
    }
}
