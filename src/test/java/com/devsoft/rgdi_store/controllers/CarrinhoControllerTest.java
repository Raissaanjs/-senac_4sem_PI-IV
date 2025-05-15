package com.devsoft.rgdi_store.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.UserGroup;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.services.CarrinhoService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CarrinhoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @MockBean
    private CarrinhoService carrinhoService;

    @BeforeEach
    public void setup() {
        clienteRepository.deleteAll();

        ClienteEntity cliente = new ClienteEntity();
        cliente.setEmail("cliente@teste.com");
        cliente.setSenha(passwordEncoder.encode("123456"));
        cliente.setGrupo(UserGroup.ROLE_CLIENT);

        clienteRepository.save(cliente);
    }

    // Seleciona frete específico e salva na sessão
    @Test
    public void deveSelecionarFreteLoggiESalvarNaSessao() throws Exception {
        // 1. Fazer login real com formLogin e obter a sessão
        MvcResult loginResult = mockMvc.perform(post("/clientes/login")
                        .param("email", "cliente@teste.com")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        // 2. Usar a sessão autenticada no teste do POST
        mockMvc.perform(post("/carrinho/frete")
                        .param("tipoFrete", "loggi")
                        .session(session)) // Usa a sessão autenticada
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/carrinho"));
    }
    
    // Retorna False quando carrinho está vazio
    @Test
    void deveRetornarFalseQuandoCarrinhoEstaVazio() throws Exception {
        // Mocka retorno de carrinho vazio
        Mockito.when(carrinhoService.getQuantidadeTotalItens()).thenReturn(0);

        // Login real
        MvcResult loginResult = mockMvc.perform(post("/clientes/login")
                        .param("email", "cliente@teste.com")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        // Requisição autenticada com sessão válida
        mockMvc.perform(get("/carrinho/verificar")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temItens").value(false));
    }
    
    //Retorna True quando carrinho tem itens
    @Test
    public void deveRetornarTrueQuandoCarrinhoTemItens() throws Exception {
        // Mocka 3 itens no carrinho
        Mockito.when(carrinhoService.getQuantidadeTotalItens()).thenReturn(3);

        // Faz login real
        MvcResult loginResult = mockMvc.perform(post("/clientes/login")
                        .param("email", "cliente@teste.com")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        // Executa a requisição autenticada
        mockMvc.perform(get("/carrinho/verificar")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temItens").value(true));
    }

}


