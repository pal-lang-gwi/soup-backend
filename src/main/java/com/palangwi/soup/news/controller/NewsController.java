package com.palangwi.soup.news.controller;

import com.palangwi.soup.common.security.JwtAuthentication;
import com.palangwi.soup.common.utils.ApiUtils.ApiResult;
import com.palangwi.soup.news.dto.DailyNewsRequestDto;
import com.palangwi.soup.news.dto.DailyNewsResponseDto;
import com.palangwi.soup.news.dto.NewsDto;
import com.palangwi.soup.news.service.NewsService;
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
@RequestMapping("/api/v1/news")
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public ApiResult<DailyNewsResponseDto> getFilteredNews(@AuthenticationPrincipal JwtAuthentication userInfo,
                                                           @Valid @ModelAttribute DailyNewsRequestDto request,
                                                           @PageableDefault(size = 20, sort = "createdDate", direction = Direction.DESC) Pageable pageable) {
        return success(newsService.getDailyNews(DailyNewsRequestDto.from(request), pageable));
    }

    @GetMapping("/{newsId}")
    public ApiResult<NewsDto> getNewsInfo(@AuthenticationPrincipal JwtAuthentication userInfo,
                                          @PathVariable(name = "newsId") String newsId) {
        return success(newsService.getNewsDetailInfo(newsId));
    }
}
