package com.palangwi.soup.schedule;

import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.keyword.Status;
import com.palangwi.soup.repository.keyword.KeywordRepository;
import com.palangwi.soup.service.news.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollectNewsScheduler {

    private final KeywordRepository keywordRepository;
    private final NewsService newsService;

    @Scheduled(cron = "0 0 7 * * *", zone = "Asia/Seoul")
    private void collectNews() {
        List<Keyword> keywords = keywordRepository.findAllByStatus(Status.ACTIVE);
        log.info("수집 시작 - 키워드의 갯수 : {}", keywords.size());

        for (Keyword keyword : keywords) {
            try {
                newsService.collectAndSendNews(keyword.getId());
                log.info("{} 키워드 뉴스 저장 성공", keyword.getName());
            } catch (Exception e) {
                log.info("{} 키워드 뉴스 저장 실패", keyword.getName(), e);
            }
        }

        log.info("{}, 전체 뉴스 데이터 수집 완료", LocalDate.now());
    }
}
