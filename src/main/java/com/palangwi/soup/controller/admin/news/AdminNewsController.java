package com.palangwi.soup.controller.admin.news;

import com.palangwi.soup.service.news.NewsService;
import com.palangwi.soup.utils.ApiUtils.ApiResult;
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
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        newsService.collectAndSaveNews(keyword);
        return null;
    }
}
