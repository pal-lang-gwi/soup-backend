package com.palangwi.soup.admin.keyword.service;

import com.palangwi.soup.admin.dto.keyword.AdminKeywordResponseListDto;
import com.palangwi.soup.admin.dto.keyword.ApproveKeywordResponseDto;
import com.palangwi.soup.admin.dto.keyword.RejectKeywordResponseDto;
import org.springframework.data.domain.Pageable;

public interface AdminKeywordRequestService {

    AdminKeywordResponseListDto getRequestedKeywords(String status, Pageable pageable);

    ApproveKeywordResponseDto approveKeyword(Long requestId);

    RejectKeywordResponseDto rejectKeyword(Long requestId, String rejectReason);
}