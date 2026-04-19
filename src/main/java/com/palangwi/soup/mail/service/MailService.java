package com.palangwi.soup.mail.service;

import com.palangwi.soup.admin.dto.email.EmailScheduleResponseDto;
import com.palangwi.soup.admin.dto.email.EmailTestResponseDto;
import com.palangwi.soup.mail.domain.MailEvent;
import com.palangwi.soup.mail.dto.MailMessage;
import com.palangwi.soup.mail.exception.MailNotFoundException;
import com.palangwi.soup.mail.infrastructure.MailViewRenderer;
import com.palangwi.soup.mail.repository.MailEventRepository;
import com.palangwi.soup.news.dto.DailyNewsMailRequestDto;
import com.palangwi.soup.news.dto.NewsForMailDto;
import com.palangwi.soup.news.dto.SummaryForMailTemplateDto;
import com.palangwi.soup.user.domain.User;
import com.palangwi.soup.user.exception.UserNotFoundException;
import com.palangwi.soup.subscription.repository.UserKeywordRepository;
import com.palangwi.soup.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static com.palangwi.soup.mail.domain.MailType.DAILY_NEWS;
import static com.palangwi.soup.mail.domain.MailType.TEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final MailEventRepository mailEventRepository;
    private final MailAsyncExecutor mailAsyncExecutor;
    private final MailViewRenderer mailViewRenderer;
    private final NewsAggregator newsAggregator;
    private final UserRepository userRepository;

    private static final long TEST_MAIL_EVENT_ID = -1L;

    private static final byte[] TRANSPARENT_PIXEL = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR4nGMAAQAABQABDQottAAAAABJRU5ErkJggg=="
    );
    private final UserKeywordRepository userKeywordRepository;

    @Transactional
    public void sendDailyNews(DailyNewsMailRequestDto request) {
        User user = request.user();
        List<Long> keywords = request.keywordIds();
        LocalDateTime now = request.now();

        log.info("메일 전송 처리 시작: userId={}, keywords={}", user.getId(), keywords);

        List<NewsForMailDto> summaryMap = newsAggregator.select(request.keywordIds());
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

        List<Long> fixedKeywordIds = List.of(1L);

        List<NewsForMailDto> newsDtos = newsAggregator.select(fixedKeywordIds);

        if (newsDtos.isEmpty()) {
            throw new MailNotFoundException();
        }

        List<SummaryForMailTemplateDto> summaryForMail = convertToMailTemplateDtos(newsDtos);

        String html = mailViewRenderer.renderDailyNews(
                user.getUsername(),
                summaryForMail,
                TEST_MAIL_EVENT_ID
        );

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

    private List<SummaryForMailTemplateDto> convertToMailTemplateDtos(List<NewsForMailDto> newsDtos) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        return newsDtos.stream()
                .map(dto -> new SummaryForMailTemplateDto(
                        dto.keywordId(),
                        dto.keywordName(),
                        renderer.render(parser.parse(dto.shortSummary() != null ? dto.shortSummary() : "")),
                        dto.createdDate()
                ))
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
