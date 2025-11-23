package com.palangwi.soup.user.controller;

import com.palangwi.soup.common.utils.ApiUtils.ApiResult;
import com.palangwi.soup.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.palangwi.soup.common.utils.ApiUtils.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/refresh")
    public ApiResult<Void> refresh(HttpServletRequest request, HttpServletResponse response) {
        authService.reissueAccessToken(request, response);
        return success();
    }

    @PostMapping("/logout")
    public ApiResult<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return success();
    }
}
