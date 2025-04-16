package com.palangwi.soup.security;

public enum Role {

    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    GUEST("ROLE_GUEST"),
    PREMIUN("ROLE_PREMIUN");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static Role of(String name) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(name)) {
                return role;
            }
        }

        return null;
    }
}