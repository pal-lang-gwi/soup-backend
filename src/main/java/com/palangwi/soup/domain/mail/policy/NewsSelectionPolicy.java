package com.palangwi.soup.domain.mail.policy;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class NewsSelectionPolicy {

    /**
     * Todo : NoSQL에서 전송할 뉴스의 정보를 가져오는 로직
     */
//    private final NewsSummeryRepository newsSummeryRepository;
//
//    public List<NewsSummary> select (List<String> keywords) {
//        LocalDate today = LocalDate.now();
//        LocalDateTime from = today.atStartOfDay();
//        LocalDateTime to = from.plusDays(1);
//
//        List<NewsSummary> summaries = newsSummeryRepository.findByCreatedAtBetween(from, to);
//
//        return summaries.stream()
//                .filter(news -> keywords.contains(news.getKeyword()))
//                .toList();
//    }
}
