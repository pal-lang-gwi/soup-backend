package com.palangwi.soup.service.news;

import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.news.News;
import com.palangwi.soup.dto.news.DailyNewsRequestDto;
import com.palangwi.soup.dto.news.DailyNewsResponseDto;
import com.palangwi.soup.dto.news.NewsDto;
import com.palangwi.soup.exception.keyword.KeywordNotFoundException;
import com.palangwi.soup.exception.news.NewsNotFoundException;
import com.palangwi.soup.repository.keyword.KeywordRepository;
import com.palangwi.soup.repository.news.NewsRedisRepository;
import com.palangwi.soup.repository.news.NewsRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    private final KeywordRepository keywordRepository;
    private final NewsRedisRepository newsRedisRepository;

    public DailyNewsResponseDto getDailyNews(DailyNewsRequestDto request, Pageable pageable) {
        Page<News> resultPage = getNews(request.keywordId(), request.startDate(), request.endDate(), pageable);

        List<NewsDto> newsDtos = resultPage.getContent().stream()
                .map(NewsDto::from)
                .toList();

        return new DailyNewsResponseDto(newsDtos, resultPage.getTotalElements(), resultPage.getTotalPages(),
                resultPage.getNumber() + 1);
    }

    private Page<News> getNews(Long keywordId, String startDate, String endDate, Pageable pageable) {
        boolean hasStart = startDate != null && !startDate.isBlank();
        boolean hasEnd = endDate != null && !endDate.isBlank();
        boolean hasKeyword = keywordId != null;

        LocalDateTime from = hasStart ? LocalDate.parse(startDate).atStartOfDay() : null;
        LocalDateTime to = hasEnd ? LocalDate.parse(endDate).plusDays(1).atStartOfDay() : null;

        Page<News> resultPage;

        resultPage = findNewsByCondition(keywordId, pageable, hasKeyword, hasStart, hasEnd, from, to);

        return resultPage;
    }

    private Page<News> findNewsByCondition(Long keywordId, Pageable pageable, boolean hasKeyword, boolean hasStart,
            boolean hasEnd,
            LocalDateTime from, LocalDateTime to) {
        Page<News> resultPage;
        if (hasKeyword && hasStart && hasEnd) {
            resultPage = newsRepository.findByCreatedDateBetweenAndKeywordId(from, to, keywordId, pageable);
        } else if (hasStart && hasEnd) {
            resultPage = newsRepository.findByCreatedDateBetween(from, to, pageable);
        } else if (hasKeyword) {
            resultPage = newsRepository.findByKeywordId(keywordId, pageable);
        } else {
            resultPage = newsRepository.findAll(pageable);
        }
        return resultPage;
    }

    public void collectAndSendNews(Long keywordId) {
        Optional<Keyword> keyword = keywordRepository.findById(keywordId);

        if (keyword.isEmpty()) {
            throw new KeywordNotFoundException();
        }

        String keywordName =  keyword.get().getName();
        newsAIService.searchAndSummarizeAsync(keywordId, keywordName)
                .thenAccept(result -> {
                    try {
                        log.info("✅ 뉴스 요약 완료: {}", result);
                        newsRedisRepository.save(keywordId, result, 6 * 3600);
                    } catch (Exception e) {
                        log.error("❌ Redis 저장 실패 - {}", keywordName, e);
                    }
                })
                .exceptionally(ex -> {
                    log.error("❌ OpenAI 응답 실패 - {}", keywordName, ex);
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