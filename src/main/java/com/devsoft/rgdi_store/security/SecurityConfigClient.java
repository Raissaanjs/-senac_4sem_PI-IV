package com.devsoft.rgdi_store.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.devsoft.rgdi_store.controllers.handlers.CustomAuthenticationFailureHandlerLoja;
import com.devsoft.rgdi_store.controllers.handlers.CustomNoAuthenticatedHandlerLoja;

@Configuration
@EnableWebSecurity
@Order(1)  // Colocando a configuração do cliente com prioridade (menor valor)
public class SecurityConfigClient {

    private final CustomNoAuthenticatedHandlerLoja customNoAuthenticatedHandlerLoja;
    private final CustomAuthenticationFailureHandlerLoja customFailureHandlerLoja;

    public SecurityConfigClient(CustomNoAuthenticatedHandlerLoja clienteInativoHandler,
                                CustomAuthenticationFailureHandlerLoja customFailureHandler) {
        this.customNoAuthenticatedHandlerLoja = clienteInativoHandler;
        this.customFailureHandlerLoja = customFailureHandler;
    }

    @Bean
    public SecurityFilterChain clientSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/clientes/**")  // Aplica segurança apenas para rotas de cliente
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/clientes/cadastrar", "/clientes/login", "/clientes/registrar").permitAll()  // Permite o cadastro e login
                .anyRequest().authenticated()  // Qualquer outra rota do cliente requer autenticação
            )
            .formLogin(form -> form
                .loginPage("/clientes/login")  // Página de login personalizada para cliente
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/carrinho", true)  // Redireciona após login bem-sucedido
                .failureHandler(customFailureHandlerLoja)  // Handler personalizado para falhas
            )
            .logout(logout -> logout
                .logoutUrl("/cliente/logout")  // URL de logout do cliente
                .logoutSuccessUrl("/")  // Redireciona após logout
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .expiredUrl("/clientes/login?expired=true")
                .maxSessionsPreventsLogin(false)
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(customNoAuthenticatedHandlerLoja)
                .accessDeniedPage("/error-no-perm")
            )
            .csrf(csrf -> csrf.disable())  // Desabilita CSRF para facilitar testes no Postman
            .headers(headers -> headers.frameOptions().sameOrigin());

        return http.build();
    }
}

