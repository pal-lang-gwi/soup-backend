package com.palangwi.soup.dto.news;

import java.util.List;

public record DailyNewsResponseDto(
        List<NewsDto> newsDtos,
        long totalElements,
        int totalPages,
        int currentPage
) {
}
