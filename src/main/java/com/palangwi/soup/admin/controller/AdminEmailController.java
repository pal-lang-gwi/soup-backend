package com.palangwi.soup.admin.controller;

import com.palangwi.soup.admin.dto.email.EmailScheduleResponseDto;
import com.palangwi.soup.admin.dto.email.EmailTestResponseDto;
import com.palangwi.soup.common.security.JwtAuthentication;
import com.palangwi.soup.common.utils.ApiUtils.ApiResult;
import com.palangwi.soup.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.palangwi.soup.common.utils.ApiUtils.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/email")
public class AdminEmailController {

    private final MailService mailService;

    @GetMapping("/test")
    public ApiResult<EmailTestResponseDto> testEmail(
            @AuthenticationPrincipal JwtAuthentication userInfo) {
        return success(mailService.testEmail(userInfo.id()));
    }

    @GetMapping("/schedule")
    public ApiResult<EmailScheduleResponseDto> getEmailSchedule(
            @AuthenticationPrincipal JwtAuthentication userInfo) {
        return success(mailService.getMailSchedule());
    }
}
