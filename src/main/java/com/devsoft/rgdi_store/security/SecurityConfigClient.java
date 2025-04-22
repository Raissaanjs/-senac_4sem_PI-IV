package com.devsoft.rgdi_store.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.devsoft.rgdi_store.controllers.handlers.CustomAuthenticationFailureHandlerCliente;
import com.devsoft.rgdi_store.controllers.handlers.CustomNoAuthenticatedHandlerCliente;

@Configuration
@EnableWebSecurity
public class SecurityConfigClient {

    private final CustomNoAuthenticatedHandlerCliente customNoAuthenticatedHandlerCliente;
    private final CustomAuthenticationFailureHandlerCliente customFailureHandlerCliente;

    public SecurityConfigClient(CustomNoAuthenticatedHandlerCliente clienteInativoHandler,
                                CustomAuthenticationFailureHandlerCliente customFailureHandler) {
        this.customNoAuthenticatedHandlerCliente = clienteInativoHandler;
        this.customFailureHandlerCliente = customFailureHandler;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain clientSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/clientes/**")  // Aplica segurança apenas para rotas de cliente
            .authorizeHttpRequests(auth -> auth        		
        		.requestMatchers("/clientes/login").permitAll()
        		.requestMatchers("/clientes/cadastrar", "/clientes/salvar-cliente/**", 
    				"/clientes/detalhes/**").permitAll()
        		.requestMatchers("/clientes/error/error-no-perm-cliente", "/clientes/error/error-cliente-inat",
        				"/clientes/error/error-login-cliente", "/clientes/error/error-no-auth-cliente").permitAll()
        		.requestMatchers("/clientes/cadastrar-enderecos/**", "/clientes/cadastrar-endereco/**",
        				"/clientes/salvar-enderecos/**", "/clientes/salvar-endereco-faturamento-inicial/**",
        				"/clientes/salvar-endereco-entrega/**").permitAll()
	            .requestMatchers("/clientes/admin/**").hasAnyAuthority("ROLE_CLIENT")
                .anyRequest().authenticated()  // Qualquer outra rota do cliente requer autenticação
            )
            .formLogin(form -> form
                .loginPage("/clientes/login")  // Página de login personalizada para cliente
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/clientes/admin", true)  // Redireciona após login bem-sucedido
                .failureHandler(customFailureHandlerCliente)  // Handler personalizado para falhas
            )
            .logout(logout -> logout
                .logoutUrl("/clientes/logout")  // URL de logout do cliente
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
                .authenticationEntryPoint(customNoAuthenticatedHandlerCliente)
                .accessDeniedPage("/clientes/error/error-no-perm-cliente")
            )
            .csrf(csrf -> csrf.disable())  // Desabilita CSRF para facilitar testes no Postman
            .headers(headers -> headers.frameOptions().sameOrigin());

        return http.build();
    }
}

