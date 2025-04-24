package com.devsoft.rgdi_store.services;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.devsoft.rgdi_store.authentication.CustomClienteDetails;
import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.repositories.ClienteRepository;

@Service("clienteUserDetailsService")
public class ClienteUserDetailsService implements UserDetailsService {

    private final ClienteRepository clienteRepository;

    public ClienteUserDetailsService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ClienteEntity cliente = clienteRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Cliente não encontrado"));

        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority(cliente.getGrupo().name())
        );

        return new CustomClienteDetails(
            cliente.getEmail(),
            cliente.getSenha(),
            authorities
        );
    }
}
