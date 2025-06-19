package com.palangwi.soup.security.userinfo;

import com.palangwi.soup.security.Role;
import java.util.Map;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final String registrationId;
//    private final String username;
    private final String email;
    private final Role role;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.registrationId = "google";
//        this.username = (String) attributes.get("email");
        this.email = (String) attributes.get("email");
        this.role = Role.of((String) attributes.get("role"));
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
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public Role getRole() {
        return role;
    }

}
