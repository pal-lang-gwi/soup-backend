package com.palangwi.soup.domain.mail.policy;

import com.palangwi.soup.domain.news.News;
import com.palangwi.soup.domain.news.Summary;
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

    public List<Summary> select (List<String> keywords) {
        LocalDate today = LocalDate.now();
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = from.plusDays(1);

        List<News> newsList = newsRepository.findByCreatedDateBetweenAndKeywordIn(from, to, keywords);

        return newsList.stream()
                .map(News::getSummary)
                .toList();
    }
}
