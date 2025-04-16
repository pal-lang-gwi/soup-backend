package com.palangwi.soup.security.userinfo;

import java.util.Map;

public interface OAuth2UserInfo {

    Map<String, Object> getAttributes();

    String getRegistrationId();

    String getUsername();

    String getNickname();
}
