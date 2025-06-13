package com.palangwi.soup.service.news;

import com.palangwi.soup.domain.news.News;
import com.palangwi.soup.dto.news.DailyNewsRequestDto;
import com.palangwi.soup.dto.news.DailyNewsResponseDto;
import com.palangwi.soup.dto.news.NewsDto;
import com.palangwi.soup.exception.news.NewsNotFoundException;
import com.palangwi.soup.repository.news.NewsRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final NewsAIService newsAIService;

    public DailyNewsResponseDto getDailyNews(DailyNewsRequestDto request, Pageable pageable) {
        Page<News> resultPage = getNews(request.keyword(), request.startDate(), request.endDate(), pageable);

        List<NewsDto> newsDtos = resultPage.getContent().stream()
                .map(NewsDto::from)
                .toList();

        return new DailyNewsResponseDto(newsDtos, resultPage.getTotalElements(), resultPage.getTotalPages(), resultPage.getNumber() + 1);
    }

    private Page<News> getNews(String keyword, String startDate, String endDate, Pageable pageable) {
        boolean hasStart = startDate != null && !startDate.isBlank();
        boolean hasEnd = endDate != null && !endDate.isBlank();
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        LocalDateTime from = hasStart ? LocalDate.parse(startDate).atStartOfDay() : null;
        LocalDateTime to = hasEnd ? LocalDate.parse(endDate).plusDays(1).atStartOfDay() : null;

        Page<News> resultPage;

        resultPage = findNewsByCondition(keyword, pageable, hasKeyword, hasStart, hasEnd, from, to);

        return resultPage;
    }

    private Page<News> findNewsByCondition(String keyword, Pageable pageable, boolean hasKeyword, boolean hasStart, boolean hasEnd,
                               LocalDateTime from, LocalDateTime to) {
        Page<News> resultPage;
        if (hasKeyword && hasStart && hasEnd) {
            resultPage = newsRepository.findByCreatedDateBetweenAndKeyword(from, to, keyword, pageable);
        } else if (hasStart && hasEnd) {
            resultPage = newsRepository.findByCreatedDateBetween(from, to, pageable);
        } else if (hasKeyword) {
            resultPage = newsRepository.findByKeyword(keyword, pageable);
        } else {
            resultPage = newsRepository.findAll(pageable);
        }
        return resultPage;
    }

    public void collectAndSaveNews(String keyword) {
        newsAIService.searchAndSummarizeAsync(keyword)
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

    public NewsDto getNewsDetailInfo(String newsId) {
        try {
            ObjectId id = new ObjectId(newsId);
            News news = newsRepository.findById(id).orElseThrow(NewsNotFoundException::new);
            return NewsDto.from(news);
        } catch (IllegalArgumentException e) {
            throw new NewsNotFoundException();
        }
    }
}