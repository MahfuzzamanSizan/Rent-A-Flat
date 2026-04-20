package com.example.rentflat.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PreferredTenant {
    BACHELOR,
    FAMILY,
    BOTH;

    @JsonCreator
    public static PreferredTenant from(String value) {
        if (value == null) return null;
        return PreferredTenant.valueOf(value.toUpperCase());
    }
}
