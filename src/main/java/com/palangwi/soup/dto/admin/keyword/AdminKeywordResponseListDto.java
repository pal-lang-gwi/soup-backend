package com.palangwi.soup.dto.admin.keyword;

import java.util.List;

public record AdminKeywordResponseListDto(
        List<AdminKeywordResponseDto> adminKeywordResponseDtos,
        int totalPages,
        long totalElements,
        int currentPage
) {}
