package com.palangwi.soup.keyword.dto;

import jakarta.validation.constraints.NotNull;

public record SubscribeKeywordRequestDto(
        @NotNull(message = "키워드는 필수 입력 항목입니다.")  Long keywordId) {
}