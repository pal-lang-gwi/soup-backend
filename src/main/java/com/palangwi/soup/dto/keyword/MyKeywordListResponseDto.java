package com.palangwi.soup.dto.keyword;

import com.palangwi.soup.domain.userkeyword.UserKeyword;

import java.util.List;
import org.springframework.data.domain.Page;

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
