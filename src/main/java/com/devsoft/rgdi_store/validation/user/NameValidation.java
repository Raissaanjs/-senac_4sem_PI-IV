package com.devsoft.rgdi_store.validation.user;

public class NameValidation {

    public static boolean isValidName(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }
        int length = name.length();
        return length >= 3 && length <= 150;
    }
}
