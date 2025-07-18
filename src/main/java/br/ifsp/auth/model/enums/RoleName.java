package br.ifsp.auth.model.enums;

import java.util.Arrays;

public enum RoleName {
    ROLE_USER,
    ROLE_ADMIN;

    public static RoleName fromString(String value) {
        return Arrays.stream(values())
                .filter(role -> role.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Role Name: " + value));
    }
}
