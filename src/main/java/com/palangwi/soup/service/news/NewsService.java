package com.palangwi.soup.service.news;

import com.palangwi.soup.domain.news.News;
import com.palangwi.soup.dto.news.DailyNewsResponseDto;
import com.palangwi.soup.dto.news.NewsDto;
import com.palangwi.soup.repository.news.NewsRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final OpenAIService openAIService;

    public DailyNewsResponseDto getDailyNews(String keyword, String startDate, String endDate, int page) {
        LocalDateTime from = LocalDateTime.parse(startDate);
        LocalDateTime to = LocalDateTime.parse(endDate);

        int size = 20;

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<News> resultPage;
        resultPage = getNews(keyword, from, to, pageable);

        List<NewsDto> newsDtos = resultPage.getContent().stream()
                .map(NewsDto::from)
                .toList();

        return new DailyNewsResponseDto(newsDtos, resultPage.getTotalElements(), resultPage.getTotalPages(), resultPage.getNumber() + 1);
    }

    private Page<News> getNews(String keyword, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Page<News> resultPage;
        if (keyword != null && !keyword.isBlank()) {
            resultPage = newsRepository.findByCreatedDateBetweenAndKeyword(from, to, keyword, pageable);
        } else {
            resultPage = newsRepository.findByCreatedDateBetween(from, to, pageable);
        }
        return resultPage;
    }

    public void collectAndSaveNews(String keyword) {
        openAIService.searchAndSummarizeAsync(keyword)
                .thenAccept(result -> {
                    try {
                        News news = result.toNews();
                        newsRepository.save(news);
                    } catch (Exception e) {
                        log.error("❌ 뉴스 파싱 실패 - {}", keyword, e);
                    }
                })
                .exceptionally(ex -> {
                    log.error("❌ OpenAI 응답 실패 - {}", keyword, ex);
                    return null;
                });
    }
}