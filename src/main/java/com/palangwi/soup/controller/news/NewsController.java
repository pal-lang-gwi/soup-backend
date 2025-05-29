package com.palangwi.soup.controller.news;

import com.palangwi.soup.dto.news.DailyNewsRequestDto;
import com.palangwi.soup.dto.news.DailyNewsResponseDto;
import com.palangwi.soup.dto.news.NewsDto;
import com.palangwi.soup.security.JwtAuthentication;
import com.palangwi.soup.service.news.NewsService;
import com.palangwi.soup.utils.ApiUtils.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.palangwi.soup.utils.ApiUtils.success;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/news")
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public ApiResult<DailyNewsResponseDto> getFilteredNews(@AuthenticationPrincipal JwtAuthentication userInfo,
                                                           @Valid @ModelAttribute DailyNewsRequestDto request) {
        return success(newsService.getDailyNews(
                request.keyword(),
                request.startDate(),
                request.endDate(),
                request.page()));
    }

    @GetMapping("/{newsId}")
    public ApiResult<NewsDto> getNewsInfo(@AuthenticationPrincipal JwtAuthentication userInfo,
                                          @PathVariable String newsId) {
        return success(newsService.getNewsDetailInfo(newsId));
    }
}
