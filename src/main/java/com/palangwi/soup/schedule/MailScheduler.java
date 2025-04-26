package com.palangwi.soup.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailScheduler {

    @Scheduled(cron = "0 0 7 * * 1-5")
    public void scheduleDailyNewsLetter() {

    }
}
