package com.palangwi.soup.dto.keyword;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record RegisterKeywordRequestDto(
        @NotEmpty(message = "키워드는 필수 입력 항목입니다.") List<String> registered) {
}