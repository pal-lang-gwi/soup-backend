package com.palangwi.soup.controller.admin.keyword;

import static com.palangwi.soup.utils.ApiUtils.success;

import com.palangwi.soup.dto.admin.keyword.AdminKeywordResponseListDto;
import com.palangwi.soup.dto.admin.keyword.ApproveKeywordResponseDto;
import com.palangwi.soup.dto.admin.keyword.RejectKeywordRequestDto;
import com.palangwi.soup.dto.admin.keyword.RejectKeywordResponseDto;
import com.palangwi.soup.security.JwtAuthentication;
import com.palangwi.soup.service.admin.keyword.AdminKeywordService;
import com.palangwi.soup.utils.ApiUtils.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/keyword-requests")
public class AdminKeywordController {

    private final AdminKeywordService adminKeywordService;

    @GetMapping
    public ApiResult<AdminKeywordResponseListDto> getRequestedKeyword(
            @AuthenticationPrincipal JwtAuthentication userInfo,
            @Valid @RequestParam String status,
            @PageableDefault(size = 10, sort = "requestedAt", direction = Direction.DESC) Pageable pageable) {
        return success(adminKeywordService.getRequestedKeywords(status, pageable));
    }

    @PostMapping("/{requestId}/approve")
    public ApiResult<ApproveKeywordResponseDto> approveRequestedKeyword(@AuthenticationPrincipal JwtAuthentication userInfo,
                                                                        @PathVariable Long requestId) {
        return success(adminKeywordService.approveKeyword(requestId));
    }

    @PostMapping("/{requestId}/reject")
    public ApiResult<RejectKeywordResponseDto> rejectRequestedKeyword(@AuthenticationPrincipal JwtAuthentication userInfo,
                                                                      @PathVariable Long requestId, @Valid @RequestBody RejectKeywordRequestDto request) {
        return success(adminKeywordService.rejectKeyword(requestId, request.rejectReason()));
    }
}
