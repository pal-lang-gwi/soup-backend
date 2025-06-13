package com.palangwi.soup.security.userinfo;

import java.util.Map;
import lombok.Getter;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final String registrationId;
    private final String username;
    private final String email;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.registrationId = "google";
        this.username = (String) attributes.get("email");
        this.email = (String) attributes.get("email");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getRegistrationId() {
        return registrationId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getNickname() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return email;
    }

}
