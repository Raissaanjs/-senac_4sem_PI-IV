package com.devsoft.rgdi_store.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.devsoft.rgdi_store.controllers.handlers.CustomAuthenticationFailureHandler;
import com.devsoft.rgdi_store.controllers.handlers.CustomNoAuthenticatedHandler;
import com.devsoft.rgdi_store.services.AdminUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final CustomNoAuthenticatedHandler customNoAuthenticatedHandler;
    private final CustomAuthenticationFailureHandler customFailureHandler;
    private final AdminUserDetailsService adminUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomNoAuthenticatedHandler usuarioInativoHandler,
                          CustomAuthenticationFailureHandler customFailureHandler,
                          AdminUserDetailsService adminUserDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.customNoAuthenticatedHandler = usuarioInativoHandler;
        this.customFailureHandler = customFailureHandler;
        this.adminUserDetailsService = adminUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }
    
	   
	@Bean
	@Order(2)
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		// Configura o provider localmente para esta chain
	    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	    authProvider.setUserDetailsService(adminUserDetailsService);
	    authProvider.setPasswordEncoder(passwordEncoder);

	    AuthenticationManagerBuilder authBuilder =
	        http.getSharedObject(AuthenticationManagerBuilder.class);
	    authBuilder.authenticationProvider(authProvider);

	    AuthenticationManager authenticationManager = authBuilder.build();
	    http.authenticationManager(authenticationManager);
		
	    
	    http
	        .authorizeHttpRequests(auth -> auth
	        	.requestMatchers("/", "/css/**", "/js/**", "/image/**", "/webjars/**", "/upload/**", "/uploads/**",
	        			"/list", "/auth").permitAll()
        		.requestMatchers("/produtos/loja/**", "/produto-imagens/imagem-principal/**" ,
            			"/produto-imagens/detalhes/**", "/produtos/detalhes/**","/carrinho/**").permitAll()
	            .requestMatchers("/login").permitAll()
	            .requestMatchers("/error-login", "/error-user-inat",
        				"/access-denied", "/error-no-perm", "/error-no-auth").permitAll()
	            .requestMatchers("/usuarios/**", "/username/**").hasAuthority("ROLE_ADMIN")
	            .requestMatchers("/produtos/listar/**","/produtos/detalhes/**" ,"/produtos/update/**").hasAnyAuthority("ROLE_ESTOQ", "ROLE_ADMIN")
	            .requestMatchers(HttpMethod.PUT, "/produtos/*/status").hasAnyAuthority("ROLE_ESTOQ", "ROLE_ADMIN")
	            .requestMatchers("/produtos/**").hasAnyAuthority("ROLE_ADMIN")	            
	            .requestMatchers("/admin").hasAnyAuthority("ROLE_ESTOQ", "ROLE_ADMIN")
	            .requestMatchers("/front-adm").hasAnyAuthority("ROLE_ESTOQ", "ROLE_ADMIN")
	            .requestMatchers("/produto-imagens/**").hasAnyAuthority("ROLE_ESTOQ", "ROLE_ADMIN")
	            .anyRequest().authenticated() // Qualquer outra rota requer autenticação
	        )
	        .httpBasic(Customizer.withDefaults()) // Habilita autenticação básica, útil para Postman
	        .formLogin(form -> form
	            .loginPage("/login")
	            .usernameParameter("email")
	            .passwordParameter("password")
	            .defaultSuccessUrl("/front-adm", true)
	            .failureHandler(customFailureHandler) // Handler personalizado para falhas
	        )
	        .logout(logout -> logout
	            .logoutUrl("/logout")
	            .logoutSuccessUrl("/login")
	            .invalidateHttpSession(true)
	            .deleteCookies("JSESSIONID")
	            .permitAll()
	        )
	        .sessionManagement(session -> session
        		.sessionFixation(sessionFixation -> sessionFixation.migrateSession())
        		.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        		.invalidSessionUrl("/login?invalid=true")
	            .maximumSessions(1)
	            .expiredUrl("/login?expired=true")
	            .maxSessionsPreventsLogin(false)
	        )
	        .exceptionHandling(exceptions -> exceptions
	            .authenticationEntryPoint(customNoAuthenticatedHandler)
	            .accessDeniedPage("/error-no-perm")
	        )
	        .csrf(csrf -> csrf.disable()) // Desabilita CSRF para facilitar testes no Postman
	        .headers(headers -> headers.frameOptions().sameOrigin());

	    return http.build();    	
    }	
	
}