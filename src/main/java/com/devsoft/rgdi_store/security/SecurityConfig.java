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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.devsoft.rgdi_store.authentication.AdminUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {	
	
    private final AdminUserDetailsService adminUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    public final SessionExpiredFilter sessionExpiredFilter;

    public SecurityConfig(AdminUserDetailsService adminUserDetailsService,
                          PasswordEncoder passwordEncoder,
                          SessionExpiredFilter sessionExpiredFilter) {
        this.adminUserDetailsService = adminUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.sessionExpiredFilter = sessionExpiredFilter;
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
	    	.addFilterBefore(sessionExpiredFilter, UsernamePasswordAuthenticationFilter.class) // Filtro para controle de Sessão
    		.authorizeHttpRequests(auth -> auth    			
	        	.requestMatchers("/", "/css/**", "/js/**", "/image/**", "/webjars/**", "/upload/**", "/uploads/**",
	        			"/list", "/auth").permitAll()
	    		.requestMatchers("/produtos/loja/**", "/produto-imagens/imagem-principal/**" ,
	        			"/produto-imagens/detalhes/**", "/produtos/detalhes/**","/carrinho/**").permitAll()
	            .requestMatchers("/login").permitAll()
	            .requestMatchers("/error-login", "/error-user-inat",
	    				"/access-denied", "/error-no-perm", "/error-no-auth").permitAll()
	            .requestMatchers("/usuarios/**", "/username/**").hasAnyAuthority("ROLE_ADMIN")
	            .requestMatchers("/produtos/listar/**","/produtos/detalhes/**",
	            		"/produtos/update/**").hasAnyAuthority("ROLE_ESTOQ", "ROLE_ADMIN")
	            .requestMatchers(HttpMethod.PUT, "/produtos/*/status").hasAnyAuthority("ROLE_ESTOQ", "ROLE_ADMIN")
	            .requestMatchers("/pedidos/admin/**").hasAnyAuthority("ROLE_ESTOQ", "ROLE_ADMIN")
	            .requestMatchers("/admin").hasAnyAuthority("ROLE_ESTOQ", "ROLE_ADMIN")
	            .requestMatchers("/front-adm").hasAnyAuthority("ROLE_ESTOQ", "ROLE_ADMIN")
	            .requestMatchers("/produtos/**").hasAnyAuthority("ROLE_ADMIN")
	            .requestMatchers("/produto-imagens/**").hasAnyAuthority("ROLE_ADMIN")
	            .anyRequest().authenticated() // Qualquer outra rota requer autenticação
	        )
	        .httpBasic(Customizer.withDefaults()) // Habilita autenticação básica, útil para Postman
	        .formLogin(form -> form
	            .loginPage("/login")
	            .usernameParameter("email")
	            .passwordParameter("password")
	            .defaultSuccessUrl("/front-adm", false) // Vai para o último endpoint acessado senão, vai para "/front-adm"
	            .failureUrl("/login?error=true")
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
	            .maximumSessions(1)
	            .maxSessionsPreventsLogin(false)
	        )
	        .exceptionHandling(exceptions -> exceptions
	            .accessDeniedPage("/error-no-perm")
	        )
	        .csrf(csrf -> csrf.disable()) // Desabilita CSRF para facilitar testes no Postman
	        .headers(headers -> headers.frameOptions().sameOrigin());

	    return http.build();    	
    }
	
	
}