package com.palangwi.soup.common.schedule;

import com.palangwi.soup.mail.service.MailService;
import com.palangwi.soup.news.dto.DailyNewsMailRequestDto;
import com.palangwi.soup.user.domain.User;
import com.palangwi.soup.user.keyword.domain.UserKeyword;
import com.palangwi.soup.user.keyword.repository.UserKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailScheduler {

    private final UserKeywordRepository userKeywordRepository;
    private final MailService mailService;

    @Scheduled(cron = "0 0 8 * * 1-5", zone = "Asia/Seoul")
    public void scheduleDailyNewsLetter() {
        log.info("뉴스 메일 발송 시작");
        LocalDateTime now = LocalDateTime.now();

        Map<User, List<Long>> userKeywordMap = getSubscribedUsersGroupedByUser();

        userKeywordMap.forEach((user, keywords) -> {
            try {
                DailyNewsMailRequestDto request = new DailyNewsMailRequestDto(user, keywords, now);
                mailService.sendDailyNews(request);
            } catch (Exception e) {
                log.error("메일 전송 실패: userId={}, error={}", user.getId(), e.getMessage(), e);
            }
        });
    }

    private Map<User, List<Long>> getSubscribedUsersGroupedByUser() {
        List<UserKeyword> userKeywords = userKeywordRepository.findAllSubscribedUserKeywords();

        log.info("userKeywords 갯수 : {}", userKeywords.size());

        return userKeywords.stream()
                .collect(Collectors.groupingBy(UserKeyword::getUser,
                        Collectors.mapping(uk -> uk.getKeyword().getId(), Collectors.toList())));
    }


}
