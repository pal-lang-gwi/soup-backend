package com.palangwi.soup.dto.news;

import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record DailyNewsRequestDto(
        Long keywordId,

        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "startDate는 yyyy-MM-dd 형식이어야 합니다.")
        String startDate,

        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "endDate는 yyyy-MM-dd 형식이어야 합니다.")
        String endDate) {
        public static DailyNewsRequestDto from(DailyNewsRequestDto dto) {
                return new DailyNewsRequestDto(dto.keywordId(), dto.startDate(), dto.endDate());
        }

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        public DailyNewsRequestDto {
                LocalDate today = LocalDate.now();
                if (startDate == null || startDate.isBlank()) {
                        startDate = today.minusMonths(1).format(FORMATTER);
                }
                if (endDate == null || endDate.isBlank()) {
                        endDate = today.format(FORMATTER);
                }
        }
}
