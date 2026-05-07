package com.palangwi.soup.admin.controller;

import com.palangwi.soup.admin.dto.keyword.AddKeywordResponseDto;
import com.palangwi.soup.admin.dto.keyword.AdminKeywordListResponseDto;
import com.palangwi.soup.admin.dto.keyword.RemoveKeywordRequestDto;
import com.palangwi.soup.admin.dto.keyword.RemoveKeywordResponseDto;
import com.palangwi.soup.admin.keyword.service.AdminKeywordService;
import com.palangwi.soup.common.security.JwtAuthentication;
import com.palangwi.soup.common.utils.ApiUtils.ApiResult;
import com.palangwi.soup.keyword.dto.RequestKeywordRequestDto;
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
@RequestMapping("/api/v1/admin/keyword")
public class AdminKeywordController {

    private final AdminKeywordService adminKeywordService;

    @GetMapping
    public ApiResult<AdminKeywordListResponseDto> getAllKeywords(
            @AuthenticationPrincipal JwtAuthentication userInfo,
            @Valid @RequestParam(name = "status", required = false) String status,
            @PageableDefault(size = 10, sort = "createdDate", direction = Direction.DESC) Pageable pageable) {
        return success(adminKeywordService.getAllKeywordList(status, pageable));
    }

    @PostMapping("/add")
    public ApiResult<AddKeywordResponseDto> approveRequestedKeyword(@AuthenticationPrincipal JwtAuthentication userInfo,
                                                                    @Valid @RequestBody RequestKeywordRequestDto requestKeywordRequestDto) {
        return success(adminKeywordService.addKeyword(requestKeywordRequestDto.keyword()));
    }

    @PostMapping("/remove")
    public ApiResult<RemoveKeywordResponseDto> rejectRequestedKeyword(@AuthenticationPrincipal JwtAuthentication userInfo,
                                                                      @Valid @RequestBody RemoveKeywordRequestDto request) {
        return success(adminKeywordService.removeKeyword(request.keywordId(), request.removeReason()));
    }
}
