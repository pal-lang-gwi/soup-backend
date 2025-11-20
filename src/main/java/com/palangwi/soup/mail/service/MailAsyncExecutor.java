package com.palangwi.soup.mail.service;

import com.palangwi.soup.mail.dto.MailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailAsyncExecutor {

    private final MailSenderService mailSenderService;

    @Async
    public void send(MailMessage mailMessage) {
        mailSenderService.sendMail(mailMessage);
    }
}
