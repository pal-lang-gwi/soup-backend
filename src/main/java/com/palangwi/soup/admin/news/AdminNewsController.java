package com.palangwi.soup.admin.news;

import com.palangwi.soup.common.utils.ApiUtils.ApiResult;
import com.palangwi.soup.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/news")
public class AdminNewsController {

    private final NewsService newsService;

    @GetMapping("/collect")
    public ApiResult<Void> collectNews(
            @RequestParam(name = "keyword", required = false) Long keywordId
    ) {
        newsService.collectNews(keywordId);
        return null;
    }
}
