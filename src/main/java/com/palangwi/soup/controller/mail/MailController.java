package com.palangwi.soup.controller.mail;

import com.palangwi.soup.service.mail.MailService;
import com.palangwi.soup.utils.ApiUtils.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mails")
public class MailController {

    private final MailService mailService;

    @GetMapping("/traking/open/{mailId}")
    public ResponseEntity<byte[]> trakingMail(@PathVariable("mailId") Long mailId) {
        byte[] pixel = mailService.trackingMail(mailId);

        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(pixel);
    }

}
