package com.devsoft.rgdi_store.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.devsoft.rgdi_store.controllers.handlers.CustomAuthenticationFailureHandler;

import com.devsoft.rgdi_store.controllers.handlers.CustomNoAuthenticatedHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final CustomNoAuthenticatedHandler customNoAuthenticatedHandler;
    private final CustomAuthenticationFailureHandler customFailureHandler;

    public SecurityConfig(CustomNoAuthenticatedHandler usuarioInativoHandler,
                          CustomAuthenticationFailureHandler customFailureHandler) {
        this.customNoAuthenticatedHandler = usuarioInativoHandler;
        this.customFailureHandler = customFailureHandler;
    }
	   
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http.securityMatcher("/**") // Aplica a segurança para todas as rotas
	        .authorizeHttpRequests(auth -> auth
	        	.requestMatchers("/upload/**", "/uploads/**", "/upload-image", "/upload-file", "/download/**", "/css/**").permitAll()
	            .requestMatchers("/", "/login", "/css/**", "/js/**", "/webjars/**",
	                             "/image/**", "/error-login", "/error-user-inat", "/access-denied",
	                             "/error-no-perm", "/error-no-auth", "/auth", "/api/files",
	                             "/upload/**", "/download/**", "/list").permitAll()
	            .requestMatchers("/auth-redirect", "/usuarios/**", "/h2-console/**").hasAuthority("ROLE_ADMIN")
	            .requestMatchers("/produtos/**").hasAnyAuthority("ROLE_ESTOQ", "ROLE_ADMIN")
	            .requestMatchers("/inventory-path").hasAnyAuthority("ROLE_ESTOQ", "ROLE_ADMIN")
	            .requestMatchers("/front-adm").hasAnyAuthority("ROLE_ESTOQ", "ROLE_ADMIN")
	            .requestMatchers("/product-images/**").hasAnyAuthority("ROLE_ESTOQ", "ROLE_ADMIN")
	            .requestMatchers("/uploadImage/**").hasAnyAuthority("ROLE_ESTOQ", "ROLE_ADMIN")
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
	            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
