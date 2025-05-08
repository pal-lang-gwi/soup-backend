package com.palangwi.soup.schedule;

import com.palangwi.soup.domain.mail.MailEvent;
import com.palangwi.soup.domain.mail.policy.NewsSelectionPolicy;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.domain.userkeyword.UserKeyword;
import com.palangwi.soup.infrastructure.mail.MailViewRenderer;
import com.palangwi.soup.repository.mail.MailEventRepository;
import com.palangwi.soup.repository.userkeyword.UserKeywordRepository;
import com.palangwi.soup.service.mail.MailAsyncExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.palangwi.soup.domain.mail.MailType.DAILY_NEWS;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailScheduler {

    private final UserKeywordRepository userKeywordRepository;
    private final NewsSelectionPolicy newsSelectionPolicy;
    private final MailViewRenderer mailViewRenderer;
    private final MailAsyncExecutor mailAsyncExecutor;
    private final MailEventRepository mailEventRepository;

    @Scheduled(cron = "0 0 7 * * 1-5", zone = "Asia/Seoul")
    public void scheduleDailyNewsLetter() {
        log.info("뉴스 메일 발송 시작");
        LocalDateTime now = LocalDateTime.now();

        Map<User, List<String>> userKeywordMap = getSubscribedUsersGroupedByUser();

        userKeywordMap.forEach((user, keywords) -> sendDailyNewsForUser(user, keywords, now));
    }

    private Map<User, List<String>> getSubscribedUsersGroupedByUser() {
        List<UserKeyword> userKeywords = userKeywordRepository.findAllSubscribedUserKeywords();

        return userKeywords.stream()
                .collect(Collectors.groupingBy(UserKeyword::getUser,
                        Collectors.mapping(uk -> uk.getKeyword().getName(), Collectors.toList())));
    }

    /**
     * Todo : NewsSummary 채우기 및 MailMessage에 들어갈 subject 정하기
     */
    private void sendDailyNewsForUser(User user, List<String> keywords, LocalDateTime now) {
//        List<NewsSummary> summaries = newsSelectionPolicy.select(keywords);
//        if (summaries.isEmpty()) return;
//
//        MailEvent mailEvent = getMailEvent(user, now);
//
//        String html = mailViewRenderer.renderDailyNews(user.getId(), summaries, mailEvent.getId());
//
//        MailMessage message = new MailMessage(
//                user.getId(),
//                user.getEmail(),
//                "오늘의 수프",
//                html,
//                DAILY_NEWS,
//                mailEvent.getId()
//        );
//
//        mailAsyncExecutor.send(message);
    }

    private MailEvent getMailEvent(User user, LocalDateTime now) {
        MailEvent mailEvent = new MailEvent(user.getId(), DAILY_NEWS, now, false);
        mailEventRepository.save(mailEvent);
        return mailEvent;
    }
}
