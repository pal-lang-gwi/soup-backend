package com.palangwi.soup.admin.keyword.service;

import com.palangwi.soup.admin.dto.keyword.AddKeywordResponseDto;
import com.palangwi.soup.admin.dto.keyword.AdminKeywordListResponseDto;
import com.palangwi.soup.admin.dto.keyword.RemoveKeywordResponseDto;
import org.springframework.data.domain.Pageable;

public interface AdminKeywordService {
    AdminKeywordListResponseDto getAllKeywordList(String status, Pageable pageable);

    AddKeywordResponseDto addKeyword(String keyword);

    RemoveKeywordResponseDto removeKeyword(Long keywordId, String rejectReason);
}
