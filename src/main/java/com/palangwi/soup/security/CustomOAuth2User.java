package com.palangwi.soup.security;

import com.palangwi.soup.security.userinfo.OAuth2UserInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2UserInfo userInfo;

    @Override
    public Map<String, Object> getAttributes() {
        return userInfo.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userInfo.getRole().value()));
    }

    @Override
    public String getName() {
        return userInfo.getUsername();
    }

    public String getNickname() {
        return userInfo.getNickname();
    }

    public String getProviderId() {
        return userInfo.getRegistrationId();
    }

    public String getEmail() {
        return userInfo.getEmail();
    }
}
