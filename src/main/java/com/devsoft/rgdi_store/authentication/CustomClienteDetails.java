package com.devsoft.rgdi_store.authentication;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomClienteDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private String username; // email
    private String password;
    private List<GrantedAuthority> authorities;

    private String nome; // novo campo para o nome do cliente

    public CustomClienteDetails(String username, String password, String nome, List<GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.authorities = authorities;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
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
        return true;
    }
}

