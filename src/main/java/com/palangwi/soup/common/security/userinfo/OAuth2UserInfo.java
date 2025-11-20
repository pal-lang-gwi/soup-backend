package com.palangwi.soup.common.security.userinfo;

import com.palangwi.soup.common.security.Role;

import java.util.Map;

public interface OAuth2UserInfo {

    Map<String, Object> getAttributes();

    String getRegistrationId();

    String getUsername();

    String getEmail();

    Role getRole();
}
