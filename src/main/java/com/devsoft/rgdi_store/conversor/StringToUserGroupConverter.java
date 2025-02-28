package com.devsoft.rgdi_store.conversor;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import com.devsoft.rgdi_store.entities.UserGroup;

@Component
public class StringToUserGroupConverter implements Converter<String, UserGroup> {

    @Override
    public UserGroup convert(String source) {
        try {
            return UserGroup.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // ou lançar uma exceção personalizada
        }
    }
}
