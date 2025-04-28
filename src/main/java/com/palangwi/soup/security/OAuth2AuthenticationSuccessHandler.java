package com.palangwi.soup.security;

import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.dto.UserInfo;
import com.palangwi.soup.security.Jwt.Claims;
import com.palangwi.soup.service.user.RefreshTokenService;
import com.palangwi.soup.service.user.UserService;
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
    private String frontDomain;

    @Value("${security.jwt.access-token.ttl}")
    private long accessTokenTtl;

    @Value("${security.jwt.refresh-token.ttl}")
    private long refreshTokenTtl;

    private final Jwt jwt;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        UserInfo userInfo = createUserInfo(authentication);
        User user = userService.loginOAuth(userInfo);

        String accessToken = createAccessToken(user);
        String refreshToken = createRefreshToken(user); // 🔥 RefreshToken 발급

        sendTokens(response, accessToken, refreshToken, user);
    }

    private UserInfo createUserInfo(Authentication authentication) {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        return new UserInfo(
                principal.getName(),
                principal.getNickname(),
                principal.getProviderId()
        );
    }

    private String createAccessToken(User user) {
        return jwt.create(
                Claims.of(
                        user.getId(),
                        new String[]{Role.USER.value()}
                )
        );
    }

    private String createRefreshToken(User user) {
        String refreshToken = java.util.UUID.randomUUID().toString();
        refreshTokenService.saveRefreshToken(refreshToken, user.getId());
        return refreshToken;
    }

    private void sendTokens(HttpServletResponse response, String accessToken, String refreshToken, User user) throws IOException {
        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofSeconds(accessTokenTtl))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofSeconds(refreshTokenTtl))
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        String encodedNickname = URLEncoder.encode(user.getNickname(), "UTF-8");
        String redirectUrl = String.format("%s/signup?nickname=%s&userId=%d", frontDomain, encodedNickname, user.getId());

        response.sendRedirect(redirectUrl);
    }
}