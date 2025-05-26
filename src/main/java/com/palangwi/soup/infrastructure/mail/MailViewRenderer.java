package com.palangwi.soup.infrastructure.mail;

import com.palangwi.soup.domain.news.Summary;
import com.palangwi.soup.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MailViewRenderer {

    private final TemplateEngine templateEngine;

    /**
     * Todo : 요약 정보 넣기
     */
    public String renderDailyNews (String username, List<Summary> summaries, Long mailEventId) {
        Context context = new Context();
        context.setVariable("name", username);
        context.setVariable("summaries", summaries);
        context.setVariable("trackingUrl", "https://trackingUrl-example.com/read?eventId=" + mailEventId);
        // resources/templates/mail/daily-news.html 파일에 적용
        return templateEngine.process("mail/daily-news", context);
    }
}
