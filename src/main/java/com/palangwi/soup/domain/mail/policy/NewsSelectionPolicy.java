package com.palangwi.soup.domain.mail.policy;

import com.palangwi.soup.domain.news.News;
import com.palangwi.soup.dto.news.NewsForMailDto;
import com.palangwi.soup.repository.news.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NewsSelectionPolicy {

    /**
     * Todo : NoSQL에서 전송할 뉴스의 정보를 가져오는 로직
     */
    private final NewsRepository newsRepository;

    public List<NewsForMailDto> select(List<Long> keywordIds) {
        LocalDate today = LocalDate.now();
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = from.plusDays(1);

        List<News> newsList = newsRepository.findByCreatedDateBetweenAndKeywordIdIn(from, to, keywordIds);

        return newsList.stream()
                .map(NewsForMailDto::from)   // DTO 변환
                .toList();
    }

}
