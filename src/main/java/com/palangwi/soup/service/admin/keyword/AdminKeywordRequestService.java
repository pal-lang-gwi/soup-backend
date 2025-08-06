package com.palangwi.soup.service.admin.keyword;

import com.palangwi.soup.dto.admin.keyword.AdminKeywordResponseListDto;
import com.palangwi.soup.dto.admin.keyword.ApproveKeywordResponseDto;
import com.palangwi.soup.dto.admin.keyword.RejectKeywordResponseDto;
import org.springframework.data.domain.Pageable;

public interface AdminKeywordRequestService {

    AdminKeywordResponseListDto getRequestedKeywords(String status, Pageable pageable);

    ApproveKeywordResponseDto approveKeyword(Long requestId);

    RejectKeywordResponseDto rejectKeyword(Long requestId, String rejectReason);
}