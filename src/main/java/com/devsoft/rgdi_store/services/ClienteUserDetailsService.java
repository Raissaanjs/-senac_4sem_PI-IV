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

// Usado na autenticação juntamente com:
// CustomClienteDetails e SecurityConfigClient
@Service("clienteUserDetailsService") // Registra essa classe como um bean de serviço no contexto da aplicação
public class ClienteUserDetailsService implements UserDetailsService {

    private final ClienteRepository clienteRepository;

    public ClienteUserDetailsService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Implementação do método obrigatório da interface UserDetailsService
    @Override    
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ClienteEntity cliente = clienteRepository.findByEmail(username) // Busca cliente por email
            .orElseThrow(() -> new UsernameNotFoundException("Cliente não encontrado")); // se não encontrar lança exceção

        // Cria uma lista de permissões (autoridades) para o cliente, baseada no grupo que ele pertence
        List<GrantedAuthority> authorities = List.of( 
            new SimpleGrantedAuthority(cliente.getGrupo().name())
        );

        // Cria e retorna um objeto CustomClienteDetails, que é uma implementação personalizada de UserDetails
        return new CustomClienteDetails(
        	    cliente.getEmail(),
        	    cliente.getSenha(),
        	    cliente.getNome(), // Específico para o Cliente
        	    authorities
        	);
    }
}
