package com.devsoft.rgdi_store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityBeansConfig {

    @Bean
    //@Qualifier("defaultPasswordEncoder")
    public PasswordEncoder defaultPasswordEncoder() {
        return new BCryptPasswordEncoder(); // Ou outro tipo de PasswordEncoder
    }
}
