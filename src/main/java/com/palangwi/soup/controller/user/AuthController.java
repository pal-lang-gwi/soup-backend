package com.palangwi.soup.controller.user;

import static com.palangwi.soup.utils.ApiUtils.success;

import com.palangwi.soup.security.Jwt;

import com.palangwi.soup.service.user.AuthService;
import com.palangwi.soup.utils.ApiUtils.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
