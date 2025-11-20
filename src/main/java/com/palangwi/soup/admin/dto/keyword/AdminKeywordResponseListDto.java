package com.palangwi.soup.admin.dto.keyword;

import java.util.List;

public record AdminKeywordResponseListDto(
        List<AdminKeywordResponseDto> adminKeywordResponseDtos,
        int totalPages,
        long totalElements,
        int currentPage
) {}
