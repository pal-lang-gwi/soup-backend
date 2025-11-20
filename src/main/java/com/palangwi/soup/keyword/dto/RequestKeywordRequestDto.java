package com.palangwi.soup.keyword.dto;

import jakarta.validation.constraints.NotEmpty;

public record RequestKeywordRequestDto(
        @NotEmpty
        String keyword) {
}
