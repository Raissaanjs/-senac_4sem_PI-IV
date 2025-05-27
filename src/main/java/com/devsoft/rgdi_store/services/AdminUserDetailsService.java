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
@Service("adminUserDetailsService") // Registra essa classe como um bean de serviço no contexto da aplicação
public class AdminUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AdminUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Implementação do método obrigatório da interface UserDetailsService
    @Override    
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity usuario = userRepository.findByEmail(username) // Busca usuário por email
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado")); // se não encontrar lança exceção

        // Cria uma lista de permissões (autoridades) para o cliente, baseada no grupo que ele pertence
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority(usuario.getGrupo().name())
        );

        // Cria e retorna um objeto CustomUserDetails, que é uma implementação personalizada de UserDetails
        return new CustomUserDetails(
            usuario.getEmail(),
            usuario.getSenha(),
            usuario.isStatus(), // Específico para o Admin
            authorities
        );
    }
}

