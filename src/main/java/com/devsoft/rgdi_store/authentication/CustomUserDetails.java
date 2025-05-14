package com.devsoft.rgdi_store.authentication;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(CustomUserDetails.class);
	
	private String username;
    private String password;
    private boolean active; // Adicione um campo para verificar se o usuário está ativo
    private List<GrantedAuthority> authorities;

    public CustomUserDetails(String username, String password, boolean active, List<GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.active = active;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Logar as autoridades carregadas para depuração
    	authorities.forEach(auth -> logger.debug("(CustomUserDetails) Autoridade carregada: {}", auth.getAuthority()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active; // Retorna o status do usuário
    }
}

