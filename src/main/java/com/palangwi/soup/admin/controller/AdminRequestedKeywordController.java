package com.palangwi.soup.admin.controller;

import com.palangwi.soup.admin.dto.keyword.AdminKeywordResponseListDto;
import com.palangwi.soup.admin.dto.keyword.ApproveKeywordResponseDto;
import com.palangwi.soup.admin.dto.keyword.RejectKeywordRequestDto;
import com.palangwi.soup.admin.dto.keyword.RejectKeywordResponseDto;
import com.palangwi.soup.admin.keyword.service.AdminKeywordRequestService;
import com.palangwi.soup.common.security.JwtAuthentication;
import com.palangwi.soup.common.utils.ApiUtils.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.palangwi.soup.common.utils.ApiUtils.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/keyword-requests")
public class AdminRequestedKeywordController {

    private final AdminKeywordRequestService adminKeywordService;

    @GetMapping
    public ApiResult<AdminKeywordResponseListDto> getRequestedKeyword(
            @AuthenticationPrincipal JwtAuthentication userInfo,
            @Valid @RequestParam(name = "status", required = false) String status,
            @PageableDefault(size = 10, sort = "createdDate", direction = Direction.DESC) Pageable pageable) {
        return success(adminKeywordService.getRequestedKeywords(status, pageable));
    }

    @PostMapping("/{requestId}/approve")
    public ApiResult<ApproveKeywordResponseDto> approveRequestedKeyword(@AuthenticationPrincipal JwtAuthentication userInfo,
                                                                        @PathVariable(name = "requestId") Long requestId) {
        return success(adminKeywordService.approveKeyword(requestId));
    }

    @PostMapping("/{requestId}/reject")
    public ApiResult<RejectKeywordResponseDto> rejectRequestedKeyword(@AuthenticationPrincipal JwtAuthentication userInfo,
                                                                      @PathVariable(name = "requestId") Long requestId, @Valid @RequestBody RejectKeywordRequestDto request) {
        return success(adminKeywordService.rejectKeyword(requestId, request.rejectReason()));
    }
}
