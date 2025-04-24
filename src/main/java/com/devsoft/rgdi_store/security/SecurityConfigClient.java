package com.devsoft.rgdi_store.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.devsoft.rgdi_store.controllers.handlers.CustomNoAuthenticatedHandlerCliente;
import com.devsoft.rgdi_store.services.ClienteUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfigClient {

    private final CustomNoAuthenticatedHandlerCliente customNoAuthenticatedHandlerCliente;
    private final ClienteUserDetailsService clienteUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfigClient(CustomNoAuthenticatedHandlerCliente clienteInativoHandler,
                                ClienteUserDetailsService clienteUserDetailsService,
                                PasswordEncoder passwordEncoder) {
        this.customNoAuthenticatedHandlerCliente = clienteInativoHandler;
        this.clienteUserDetailsService = clienteUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain clientSecurityFilterChain(HttpSecurity http) throws Exception {
    	
    	// Associa o provider ao SecurityBuilder apenas para este chain
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(clienteUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        
        AuthenticationManagerBuilder authBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.authenticationProvider(authProvider);
        
        AuthenticationManager authenticationManager = authBuilder.build();
        http.authenticationManager(authenticationManager);
        
    	
        http.securityMatcher("/clientes/**")  // Aplica segurança apenas para rotas de cliente
            .authorizeHttpRequests(auth -> auth        		
        		.requestMatchers("/clientes/login").permitAll()
        		.requestMatchers("/clientes/cadastrar/**", "/clientes/salvar-cliente/**", 
    				"/clientes/detalhes/**").permitAll()
        		.requestMatchers("/clientes/error/**").permitAll()
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
                .defaultSuccessUrl("/clientes/admin", false) // Vai para o último endpoint acessado, senão vai para "/clientes/admin"
            )
            .logout(logout -> logout
                .logoutUrl("/clientes/logout")  // URL de logout do cliente
                .logoutSuccessUrl("/")  // Redireciona após logout
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
        		.sessionFixation(sessionFixation -> sessionFixation.migrateSession())  // Cria uma nova sessão após o login
            	.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            	.invalidSessionUrl("/clientes/login?invalid=true") // URL para redirecionar quando a sessão for inválida
                .maximumSessions(1) // Define quantos usuários podem estar logados ao mesmo tempo
                .maxSessionsPreventsLogin(false) // Permite que a última sessão expirada seja reautenticada
                
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

