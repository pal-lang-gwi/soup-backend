package com.palangwi.soup.security;

import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.dto.UserInfo;
import com.palangwi.soup.security.Jwt.Claims;
import com.palangwi.soup.service.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.Duration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${front.domain}")
    private String FRONT_SERVER_DOMAIN;

    private final Jwt jwt;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        UserInfo userInfo = createUserInfo(authentication);
        User user = userService.loginOAuth(userInfo);

        sendToken(response, createJWT(user), user);
    }

    private UserInfo createUserInfo(Authentication authentication) {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();

        return new UserInfo(
                principal.getName(),
                principal.getNickname(),
                principal.getProviderId()
        );
    }

    private String createJWT(User user) {
        return jwt.create(
                Claims.of(
                        user.getId(),
                        new String[]{Role.USER.value()}
                )
        );
    }

    private void sendToken(HttpServletResponse response, String token, User user) throws IOException {
        ResponseCookie cookie = ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();

        response.setHeader("Set-Cookie", cookie.toString());

        String encodedNickname = URLEncoder.encode(user.getNickname(), "UTF-8");

        response.sendRedirect(FRONT_SERVER_DOMAIN + "/signup?nickname=" + encodedNickname + "&userId=" + user.getId());
    }
}
