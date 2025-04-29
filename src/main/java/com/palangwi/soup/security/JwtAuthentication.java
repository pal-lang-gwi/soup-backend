package com.palangwi.soup.security;

import java.security.Principal;

public record JwtAuthentication(Long id) implements Principal {

    @Override
    public String getName() {
        return id.toString(); // 또는 적절한 String 반환
    }
}