package com.devsoft.rgdi_store.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.devsoft.rgdi_store.authentication.CustomUserDetails;
import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ClienteRepository clienteRepository;

    public CustomUserDetailsService(UserRepository userRepository, ClienteRepository clienteRepository) {
        this.userRepository = userRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Primeiro, tenta encontrar o usuário no repositório de usuários
        Optional<UserEntity> usuarioOpt = userRepository.findByEmail(username);
        if (usuarioOpt.isPresent()) {
            UserEntity usuario = usuarioOpt.get();
            List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(usuario.getGrupo().name()) // Usa o nome do enum, como ROLE_ADMIN
            );
            System.out.println("CustomUserDetailsService - Autoridades carregadas: " + authorities);

            return new CustomUserDetails(
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.isStatus(), // Verifica o status ativo/inativo
                authorities
            );
        }

        // Se não encontrar no repositório de usuários, tenta encontrar no repositório de clientes
        Optional<ClienteEntity> clienteOpt = clienteRepository.findByEmail(username);
        if (clienteOpt.isPresent()) {
            ClienteEntity cliente = clienteOpt.get();
            List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_CLIENT") // Define a autoridade para clientes
            );
            System.out.println("CustomUserDetailsService - Autoridades carregadas: " + authorities);

            return new CustomUserDetails(
                cliente.getEmail(),
                cliente.getSenha(),
                true, // Verifica o status ativo/inativo
                authorities
            );
        }

        // Se não encontrar em nenhum dos repositórios, lança uma exceção
        throw new UsernameNotFoundException("Usuário não encontrado");
    }
}



