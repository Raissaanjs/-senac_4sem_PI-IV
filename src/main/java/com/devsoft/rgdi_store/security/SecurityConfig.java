package com.devsoft.rgdi_store.security;

/*
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests()
                .requestMatchers("/login", "/webjars/**", "/css/**").permitAll()
                .anyRequest().hasAnyRole("ADMIN", "ESTOQUE")
                .and()
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/access-denied")
                .usernameParameter("email")
                .passwordParameter("senha")
                .permitAll()
                .and()
            .logout()
                .permitAll()
                .and()
            .exceptionHandling().accessDeniedPage("/access-denied");
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userAdmin = User.withDefaultPasswordEncoder()
            .username("admin@example.com")
            .password("12345")
            .roles("ADMIN")
            .build();
        UserDetails userEstoque = User.withDefaultPasswordEncoder()
            .username("estoque@example.com")
            .password("senha")
            .roles("ESTOQUE")
            .build();
        UserDetails userUser = User.withDefaultPasswordEncoder()
            .username("user@example.com")
            .password("senha")
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(userAdmin, userEstoque, userUser);
    }
}

*/
