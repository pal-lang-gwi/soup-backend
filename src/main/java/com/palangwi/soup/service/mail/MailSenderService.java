package com.palangwi.soup.service.mail;

import com.palangwi.soup.domain.mail.MailError;
import com.palangwi.soup.domain.mail.MailEvent;
import com.palangwi.soup.dto.mail.MailMessage;
import com.palangwi.soup.infrastructure.mail.MailMimeMessageCreator;
import com.palangwi.soup.repository.mail.MailErrorRepository;
import com.palangwi.soup.repository.mail.MailEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailSenderService extends AbstractMailSender<MailMessage, MailMimeMessageCreator> {

    private final MailEventRepository mailEventRepository;
    private final MailErrorRepository mailErrorRepository;

    public MailSenderService(JavaMailSender javaMailSender, MailMimeMessageCreator mimeMessageCreator, MailEventRepository mailEventRepository, MailErrorRepository mailErrorRepository) {
        super(javaMailSender, mimeMessageCreator);
        this.mailEventRepository = mailEventRepository;
        this.mailErrorRepository = mailErrorRepository;
    }

    @Override
    protected void logSending(MailMessage message) {
        log.info("메일을 전송합니다. userId = {} email = {} subject = {} type = {}", message.userId(), message.to(), message.subject(), message.type());
    }

    @Override
    protected void handleSuccess(MailMessage message) {
        mailEventRepository.findById(message.mailEventId())
                        .ifPresent(MailEvent::markSuccess);
    }

    @Override
    protected void handleFailure(MailMessage message, Exception e) {
        String code = e.getClass().getSimpleName();
        String reason = e.getMessage();
        MailError error = mailErrorRepository.save(MailError.of(code, reason));

        mailEventRepository.findById(message.mailEventId())
                .ifPresent(mailEvent -> {
                    mailEvent.markFailureWithReason(error);
                });
    }
}
