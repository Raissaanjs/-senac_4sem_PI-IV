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
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.devsoft.rgdi_store.authentication.ClienteUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfigClient {	
    
    private final ClienteUserDetailsService clienteUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    public final SessionExpiredFilter sessionExpiredFilter;

    public SecurityConfigClient(ClienteUserDetailsService clienteUserDetailsService,
                                PasswordEncoder passwordEncoder,
                                SessionExpiredFilter sessionExpiredFilter) {
        this.clienteUserDetailsService = clienteUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.sessionExpiredFilter = sessionExpiredFilter;
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
        
    	
        http.securityMatcher("/clientes/**", "/pedidos/clientes/**", "/pagamentos/**", "/enderecos/**")  // Aplica segurança apenas para rotas de cliente
        	.addFilterBefore(sessionExpiredFilter, UsernamePasswordAuthenticationFilter.class) // Filtro para controle de Sessão
            .authorizeHttpRequests(auth -> auth        		
        		.requestMatchers("/clientes/noauth/**", "/enderecos/noauth/**").permitAll()
        		.requestMatchers("/clientes/error/**").permitAll()
        		.requestMatchers("/clientes/login").permitAll()
	            .requestMatchers("/clientes/auth/**").hasAnyAuthority("ROLE_CLIENT")
	            .requestMatchers("/pedidos/clientes**","/pagamentos/**", "/enderecos/auth/**").hasAuthority("ROLE_CLIENT")
                .anyRequest().authenticated()  // Qualquer outra rota do cliente requer autenticação
            )
            .formLogin(form -> form
                .loginPage("/clientes/login")  // Página de login personalizada para cliente
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(clienteAuthSuccessHandler()) // Vai para o último endpoint acessado, senão vai para o @Bean
                .failureUrl("/clientes/login?error=true")
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
                .maximumSessions(1) // Define quantos usuários podem estar logados ao mesmo tempo
                .maxSessionsPreventsLogin(false) // Permite que a última sessão expirada seja reautenticada
                
            )
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/clientes/error/error-no-perm-cliente")
            )
            .csrf(csrf -> csrf.disable())  // Desabilita CSRF para facilitar testes no Postman
            .headers(headers -> headers.frameOptions().sameOrigin());

        return http.build();
    }
    
    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler clienteAuthSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/pedidos/clientes/meus-pedidos");
        return handler;
    }

}

