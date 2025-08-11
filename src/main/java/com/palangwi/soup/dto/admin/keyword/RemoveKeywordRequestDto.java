package com.palangwi.soup.dto.admin.keyword;

import jakarta.validation.constraints.NotEmpty;

public record RemoveKeywordRequestDto(
        Long keywordId,

        @NotEmpty(message = "삭제 사유는 필수 입력 항목입니다.")
        String removeReason) {
}
