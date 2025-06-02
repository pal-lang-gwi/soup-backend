package com.palangwi.soup.controller.admin.email;

import static com.palangwi.soup.utils.ApiUtils.success;

import com.palangwi.soup.dto.admin.email.EmailScheduleResponseDto;
import com.palangwi.soup.dto.admin.email.EmailTestResponseDto;
import com.palangwi.soup.security.JwtAuthentication;
import com.palangwi.soup.service.mail.MailService;
import com.palangwi.soup.utils.ApiUtils.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/email")
public class EmailController {

    private final MailService mailService;

    @GetMapping("/test")
    public ApiResult<EmailTestResponseDto> emailTest(
            @AuthenticationPrincipal JwtAuthentication userInfo) {
        return success(mailService.testMail(userInfo.id()));
    }

    @GetMapping("/schedule")
    public ApiResult<EmailScheduleResponseDto> emailSchedule(
            @AuthenticationPrincipal JwtAuthentication userInfo) {
        return success(mailService.getMailSchedule());
    }
}
