package com.palangwi.soup.news.dto;

import java.util.List;

public record DailyNewsResponseDto(
        List<NewsDto> newsDtos,
        long totalElements,
        int totalPages,
        int currentPage
) {
}
