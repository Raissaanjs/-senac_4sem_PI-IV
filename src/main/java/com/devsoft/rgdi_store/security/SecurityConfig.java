package com.devsoft.rgdi_store.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import com.devsoft.rgdi_store.controllers.handlers.CustomAuthenticationFailureHandler;
import com.devsoft.rgdi_store.controllers.handlers.CustomAuthenticationFailureHandlerLoja;
import com.devsoft.rgdi_store.controllers.handlers.CustomNoAuthenticatedHandler;
import com.devsoft.rgdi_store.controllers.handlers.CustomNoAuthenticatedHandlerLoja;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final CustomNoAuthenticatedHandler customNoAuthenticatedHandler;
    private final CustomAuthenticationFailureHandler customFailureHandler;
    private final CustomNoAuthenticatedHandlerLoja customNoAuthenticatedHandlerLoja;
    private final CustomAuthenticationFailureHandlerLoja customFailureHandlerLoja;

    public SecurityConfig(CustomNoAuthenticatedHandler usuarioInativoHandler,
                          CustomAuthenticationFailureHandler customFailureHandler, CustomNoAuthenticatedHandlerLoja
                          clienteInativoHandler, CustomAuthenticationFailureHandlerLoja customFailureHandlerLoja) {
        this.customNoAuthenticatedHandler = usuarioInativoHandler;
        this.customFailureHandler = customFailureHandler;
        this.customNoAuthenticatedHandlerLoja = clienteInativoHandler;
        this.customFailureHandlerLoja = customFailureHandlerLoja;
    }
    
	   
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http.securityMatcher("/**") // Aplica a segurança para todas as rotas
	        .authorizeHttpRequests(auth -> auth
        	.requestMatchers("/css/**", "/js/**", "/image/**", "/webjars/**", "/upload/**", "/uploads/**", "/list", "/auth").permitAll()
        		.requestMatchers("/", "/clientes/cadastrar", "/clientes/salvar-cliente", "/clientes/salvar-enderecos/**", "/clientes/cadastrar-endereco/**", "/clientes/detalhes/**").permitAll()
        		.requestMatchers("/produtos/loja/**", "/produto-imagens/imagem-principal/**" ,
            			"/produto-imagens/detalhes/**", "/produtos/detalhes/**","/carrinho/**").permitAll()
	            .requestMatchers("/login", "/clientes/login").permitAll()
	            .requestMatchers("/error-login", "/error-user-inat",
        				"/access-denied", "/error-no-perm", "/error-no-auth").permitAll()
	            .requestMatchers("/usuarios/**", "/h2-console/**", "/username/**").hasAuthority("ROLE_ADMIN")
	            .requestMatchers("/produtos/**").hasAnyAuthority("ROLE_ESTOQ", "ROLE_ADMIN")
	            .requestMatchers("/admin").hasAnyAuthority("ROLE_ESTOQ", "ROLE_ADMIN")
	            .requestMatchers("/cliente").hasAnyAuthority("ROLE_USER")
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
}