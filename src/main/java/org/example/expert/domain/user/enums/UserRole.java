package org.example.expert.domain.user.enums;

import org.example.expert.domain.common.exception.InvalidRequestException;

import java.util.Arrays;

public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("유효하지 않은 UerRole"));
    }
}
