package com.palangwi.soup.service.mail;

import com.palangwi.soup.domain.mail.MailEvent;
import com.palangwi.soup.domain.mail.MailType;
import com.palangwi.soup.domain.mail.policy.NewsSelectionPolicy;
import com.palangwi.soup.domain.news.Summary;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.dto.admin.email.EmailScheduleResponseDto;
import com.palangwi.soup.dto.admin.email.EmailTestResponseDto;
import com.palangwi.soup.dto.mail.MailMessage;
import com.palangwi.soup.dto.news.DailyNewsMailRequestDto;
import com.palangwi.soup.dto.news.SummaryForMailTemplateDto;
import com.palangwi.soup.exception.mail.MailNotFoundException;
import com.palangwi.soup.exception.user.UserNotFoundException;
import com.palangwi.soup.infrastructure.mail.MailViewRenderer;
import com.palangwi.soup.repository.mail.MailEventRepository;
import com.palangwi.soup.repository.user.UserRepository;
import com.palangwi.soup.repository.userkeyword.UserKeywordRepository;
import com.palangwi.soup.utils.ApiUtils.ApiResult;
import java.util.Base64;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

import static com.palangwi.soup.domain.mail.MailType.DAILY_NEWS;
import static com.palangwi.soup.domain.mail.MailType.TEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final MailEventRepository mailEventRepository;
    private final MailAsyncExecutor mailAsyncExecutor;
    private final MailViewRenderer mailViewRenderer;
    private final NewsSelectionPolicy newsSelectionPolicy;
    private final UserRepository userRepository;

    private static final long TEST_MAIL_EVENT_ID = -1L;

    private static final byte[] TRANSPARENT_PIXEL = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR4nGMAAQAABQABDQottAAAAABJRU5ErkJggg=="
    );
    private final UserKeywordRepository userKeywordRepository;

    @Transactional
    public void sendDailyNews(DailyNewsMailRequestDto request) {
        User user = request.user();
        List<String> keywords = request.keywords();
        LocalDateTime now = request.now();

        log.info("메일 전송 처리 시작: userId={}, keywords={}", user.getId(), keywords);

        Map<String, Summary> summaryMap = newsSelectionPolicy.select(request.keywords());
        if (summaryMap.isEmpty()) return;

        MailEvent mailEvent = createMailEvent(user, now);

        List<SummaryForMailTemplateDto> summaryForMail = convertToMailTemplateDtos(summaryMap);

         String html = mailViewRenderer.renderDailyNews(user.getUsername(), summaryForMail, mailEvent.getId());

        MailMessage message = MailMessage.of(
                user,
                "오늘의 수프",
                html,
                DAILY_NEWS,
                mailEvent.getId()
        );

        mailAsyncExecutor.send(message);
    }

    private MailEvent createMailEvent(User user, LocalDateTime now) {
        MailEvent mailEvent = new MailEvent(user.getId(), DAILY_NEWS, now, false);
        mailEventRepository.save(mailEvent);
        return mailEvent;
    }

    @Transactional
    public byte[] trackingMail(Long mailId) {
        Optional<MailEvent> eventOpt = mailEventRepository.findById(mailId);

        if (eventOpt.isPresent()) {
            MailEvent mailEvent = eventOpt.get();
            mailEvent.markAsOpen();
            mailEventRepository.save(mailEvent);
        } else {
            log.warn("존재 하지 않는 MailID : {} 에 대한 추적 요청이 발생했습니다.", mailId);
        }

        return getTransparentPixel();
    }

    private byte[] getTransparentPixel() {
        return TRANSPARENT_PIXEL;
    }

    public EmailTestResponseDto testEmail(Long id) {
        User user = findUserById(id);
        LocalDateTime sentAt = LocalDateTime.now();

        List<String> fixedKeywords = List.of("AI");
        Map<String, Summary> summaryMap = newsSelectionPolicy.select(fixedKeywords);

        if (summaryMap.isEmpty()) {
            throw new MailNotFoundException();
        }

        List<SummaryForMailTemplateDto> summaryForMail = convertToMailTemplateDtos(summaryMap);

        String html = mailViewRenderer.renderDailyNews(user.getUsername(), summaryForMail, TEST_MAIL_EVENT_ID);

        MailMessage message = MailMessage.of(
                user,
                "[테스트] 오늘의 수프",
                html,
                TEST,
                TEST_MAIL_EVENT_ID
        );

        mailAsyncExecutor.send(message);

        return EmailTestResponseDto.of(user, sentAt);
    }

    private List<SummaryForMailTemplateDto> convertToMailTemplateDtos(Map<String, Summary> summaryMap) {
        return summaryMap.entrySet().stream()
                .map(entry -> {
                    Summary summary = entry.getValue();
                    return new SummaryForMailTemplateDto(
                            entry.getKey(),
                            summary.getLongSummary(),
                            summary.getCreatedDate().toLocalDate()
                    );
                })
                .toList();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    public EmailScheduleResponseDto getMailSchedule() {
        Optional<MailEvent> lastEventLog = mailEventRepository.findTopByTypeOrderByCreatedDateDesc(DAILY_NEWS);

        String lastStatus = lastEventLog.map(e -> e.isSendSuccess() ? "SUCCESS" : "FAIL").orElseThrow(MailNotFoundException::new);
        String lastExecutionTime = lastEventLog.map(e -> e.getSentAt().toString()).orElseThrow(MailNotFoundException::new);

        String nextExecutionTime = calculateNextExecutionTime();
        int activeTasks = userKeywordRepository.countDistinctSubscribedUsers();

        return EmailScheduleResponseDto.of(lastStatus, lastExecutionTime, nextExecutionTime, activeTasks);
    }

    private String calculateNextExecutionTime() {
        return LocalDateTime.now()
                .withHour(8).withMinute(0).withSecond(0).withNano(0)
                .plusDays(1)
                .toString();
    }
}
