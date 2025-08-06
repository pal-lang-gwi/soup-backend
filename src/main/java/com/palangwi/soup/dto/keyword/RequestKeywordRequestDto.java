package com.palangwi.soup.dto.keyword;

import jakarta.validation.constraints.NotEmpty;

public record RequestKeywordRequestDto(
        @NotEmpty
        String keyword) {
}
