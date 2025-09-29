package com.palangwi.soup.dto.keyword;

import jakarta.validation.constraints.NotEmpty;

public record SubscribeKeywordRequestDto(
        @NotEmpty(message = "키워드는 필수 입력 항목입니다.") Long keywordId) {
}