package com.palangwi.soup.service.admin.keyword;

import com.palangwi.soup.dto.admin.keyword.AddKeywordResponseDto;
import com.palangwi.soup.dto.admin.keyword.ApproveKeywordResponseDto;
import com.palangwi.soup.dto.admin.keyword.RejectKeywordResponseDto;
import com.palangwi.soup.dto.admin.keyword.RemoveKeywordResponseDto;
import com.palangwi.soup.dto.keyword.KeywordListResponseDto;
import org.springframework.data.domain.Pageable;

public interface AdminKeywordService {
    KeywordListResponseDto getAllKeywordList(String status, Pageable pageable);

    AddKeywordResponseDto addKeyword(String keyword);

    RemoveKeywordResponseDto removeKeyword(Long keywordId, String rejectReason);
}
