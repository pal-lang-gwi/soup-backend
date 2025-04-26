package com.palangwi.soup.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailRetryScheduler {

    @Scheduled(cron = "0 0 8 * * 1-5")
    public void retryFailedMails() {

    }
}
