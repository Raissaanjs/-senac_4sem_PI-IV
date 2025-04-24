package com.devsoft.rgdi_store.services;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.devsoft.rgdi_store.authentication.CustomUserDetails;
import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.repositories.UserRepository;

// Usado na autenticação juntamente com:
// CustomUserDetails e SecurityConfig
@Service("adminUserDetailsService")
public class AdminUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AdminUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity usuario = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority(usuario.getGrupo().name())
        );

        return new CustomUserDetails(
            usuario.getEmail(),
            usuario.getSenha(),
            usuario.isStatus(),
            authorities
        );
    }
}

