package com.palangwi.soup.dto.news;

import jakarta.validation.constraints.Pattern;

public record DailyNewsRequestDto(
        Long keywordId,

        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "startDate는 yyyy-MM-dd 형식이어야 합니다.")
        String startDate,

        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "endDate는 yyyy-MM-dd 형식이어야 합니다.")
        String endDate) {
        public static DailyNewsRequestDto from(DailyNewsRequestDto dto) {
                return new DailyNewsRequestDto(dto.keywordId(), dto.startDate(), dto.endDate());
        }
}
