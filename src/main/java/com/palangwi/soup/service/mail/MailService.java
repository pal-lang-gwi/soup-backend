package com.palangwi.soup.service.mail;

import com.palangwi.soup.domain.mail.MailEvent;
import com.palangwi.soup.domain.mail.MailType;
import com.palangwi.soup.domain.mail.policy.NewsSelectionPolicy;
import com.palangwi.soup.domain.news.Summary;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.dto.mail.MailMessage;
import com.palangwi.soup.dto.news.DailyNewsMailRequestDto;
import com.palangwi.soup.dto.news.SummaryForMailTemplateDto;
import com.palangwi.soup.infrastructure.mail.MailViewRenderer;
import com.palangwi.soup.repository.mail.MailEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.palangwi.soup.domain.mail.MailType.DAILY_NEWS;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final MailEventRepository mailEventRepository;
    private final MailAsyncExecutor mailAsyncExecutor;
    private final MailViewRenderer mailViewRenderer;
    private final NewsSelectionPolicy newsSelectionPolicy;

    @Transactional
    public void sendDailyNews(DailyNewsMailRequestDto request) {
        User user = request.user();
        List<String> keywords = request.keywords();
        LocalDateTime now = request.now();

        log.info("메일 전송 처리 시작: userId={}, keywords={}", user.getId(), keywords);

        Map<String, Summary> summaryMap = newsSelectionPolicy.select(request.keywords());
        if (summaryMap.isEmpty()) return;

        MailEvent mailEvent = getMailEvent(user, now);

        List<SummaryForMailTemplateDto> summaryForMail = summaryMap.entrySet().stream()
                .map(entry -> new SummaryForMailTemplateDto(
                        entry.getKey(),
                        entry.getValue().getShortSummary()
                ))
                .toList();

         String html = mailViewRenderer.renderDailyNews(user.getUsername(), summaryForMail, mailEvent.getId());

        MailMessage message = new MailMessage(
                user.getId(),
                user.getEmail(),
                "오늘의 수프",
                html,
                DAILY_NEWS,
                mailEvent.getId()
        );

        mailAsyncExecutor.send(message);
    }

    private MailEvent getMailEvent(User user, LocalDateTime now) {
        MailEvent mailEvent = new MailEvent(user.getId(), DAILY_NEWS, now, false);
        mailEventRepository.save(mailEvent);
        return mailEvent;
    }
}
