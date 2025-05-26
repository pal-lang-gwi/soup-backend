package com.palangwi.soup.schedule;

import com.palangwi.soup.domain.mail.MailEvent;
import com.palangwi.soup.domain.mail.policy.NewsSelectionPolicy;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.domain.userkeyword.UserKeyword;
import com.palangwi.soup.dto.mail.MailMessage;
import com.palangwi.soup.dto.news.DailyNewsMailRequestDto;
import com.palangwi.soup.infrastructure.mail.MailViewRenderer;
import com.palangwi.soup.repository.mail.MailEventRepository;
import com.palangwi.soup.repository.userkeyword.UserKeywordRepository;
import com.palangwi.soup.service.mail.MailAsyncExecutor;
import com.palangwi.soup.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    private final MailService mailService;

    @Scheduled(cron = "0 0 7 * * 1-5", zone = "Asia/Seoul")
    public void scheduleDailyNewsLetter() {
        log.info("뉴스 메일 발송 시작");
        LocalDateTime now = LocalDateTime.now();

        Map<User, List<String>> userKeywordMap = getSubscribedUsersGroupedByUser();

        userKeywordMap.forEach((user, keywords) -> {
            try {
                DailyNewsMailRequestDto request = new DailyNewsMailRequestDto(user, keywords, now);
                mailService.sendDailyNews(request);
            } catch (Exception e) {
                log.error("메일 전송 실패: userId={}, error={}", user.getId(), e.getMessage(), e);
            }
        });
    }

    private Map<User, List<String>> getSubscribedUsersGroupedByUser() {
        List<UserKeyword> userKeywords = userKeywordRepository.findAllSubscribedUserKeywords();

        log.info("userKeywords 갯수 : {}", userKeywords.size());

        return userKeywords.stream()
                .collect(Collectors.groupingBy(UserKeyword::getUser,
                        Collectors.mapping(uk -> uk.getKeyword().getName(), Collectors.toList())));
    }


}
