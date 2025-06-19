package com.palangwi.soup.security.userinfo;

import com.palangwi.soup.security.Role;
import java.util.Map;

public interface OAuth2UserInfo {

    Map<String, Object> getAttributes();

    String getRegistrationId();

    String getUsername();

    String getEmail();

    Role getRole();
}
