package com.devsoft.rgdi_store.controllers;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.EnderecoTipo;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.services.ClienteService;
import com.devsoft.rgdi_store.utility.ClienteAutenticadoHelper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @MockBean private ClienteService clienteService;
    @MockBean private ClienteAutenticadoHelper clienteHelper;

    @AfterEach
    public void tearDown() {
        clienteRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "joao@email.com", authorities = {"ROLE_CLIENT"})
    public void deveExibirDetalhesDoClienteLogado() throws Exception {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setNome("João");
        cliente.setEmail("joao@email.com");
        cliente.setEnderecos(new ArrayList<>());
        
        EnderecoEntity enderecoFaturamento = new EnderecoEntity();
        enderecoFaturamento.setTipo(EnderecoTipo.FATURAMENTO);
        cliente.setEnderecos(Collections.singletonList(enderecoFaturamento));

        when(clienteService.findClienteById(anyLong())).thenReturn(cliente);

        when(clienteHelper.getClienteLogado("joao@email.com")).thenReturn(cliente);

        Principal principal = () -> "joao@email.com";

        mockMvc.perform(get("/clientes/auth/detalhes-cliente").principal(principal))
                .andExpect(status().isOk())
                .andExpect(view().name("cliente/detalhes"))
                .andExpect(model().attributeExists("cliente"))
                .andExpect(model().attributeExists("enderecoFaturamento"))
                .andExpect(model().attributeExists("enderecosEntrega"));
    }
    
    @Test
    public void deveExibirPaginaLoginCliente() throws Exception {
        mockMvc.perform(get("/clientes/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login-cliente"));
    }
    
    @Test
    public void deveExibirPaginaCadastroCliente() throws Exception {
        mockMvc.perform(get("/clientes/noauth/cadastrar"))
                .andExpect(status().isOk())
                .andExpect(view().name("/cliente/cadcliente"));
    }

}

