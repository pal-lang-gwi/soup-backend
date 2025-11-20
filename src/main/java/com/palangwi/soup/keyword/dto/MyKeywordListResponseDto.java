package com.palangwi.soup.keyword.dto;

import com.palangwi.soup.subscription.domain.UserKeyword;
import org.springframework.data.domain.Page;

import java.util.List;

public record MyKeywordListResponseDto(
        List<MyKeywordDto> myKeywordDtos,
        long totalElements,
        int totalPages,
        int currentPage
) {
    public static MyKeywordListResponseDto of(Page<UserKeyword> userKeywordPage, List<MyKeywordDto> myKeywordDtos) {
        return new MyKeywordListResponseDto(
                myKeywordDtos,
                userKeywordPage.getTotalElements(),
                userKeywordPage.getTotalPages(),
                userKeywordPage.getNumber() + 1
        );
    }
}
