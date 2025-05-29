package com.palangwi.soup.dto.news;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record DailyNewsRequestDto(
        String keyword,

        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "startDate는 yyyy-MM-dd 형식이어야 합니다.")
        String startDate,

        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "endDate는 yyyy-MM-dd 형식이어야 합니다.")
        String endDate,

        @Min(value = 0, message = "page는 0 이상이어야 합니다.")
        int page) {
}
