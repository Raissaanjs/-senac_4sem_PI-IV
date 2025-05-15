package com.devsoft.rgdi_store.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.UserGroup;
import com.devsoft.rgdi_store.repositories.ClienteRepository;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        clienteRepository.deleteAll();

        ClienteEntity cliente = new ClienteEntity();
        cliente.setEmail("cliente@teste.com");
        cliente.setSenha(passwordEncoder.encode("123456"));
        cliente.setGrupo(UserGroup.ROLE_CLIENT); // Aqui está a chave

        clienteRepository.save(cliente);
    }


    // Logar e manter sessão
    @Test
    public void deveLogarEManterSessaoComUsuarioReal() throws Exception {
        MvcResult result = mockMvc.perform(post("/clientes/login")
                .param("email", "cliente@teste.com")
                .param("password", "123456")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);

        mockMvc.perform(get("/pedidos/clientes/meus-pedidos")
                .session(session))
                .andExpect(status().isOk());
    }
    
    // Login com credencial inválida
    @Test
    public void deveRetornarErroDeLoginComCredenciaisInvalidas() throws Exception {
        mockMvc.perform(post("/clientes/login")
                .param("email", "naoexiste@teste.com")
                .param("password", "errada")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/clientes/login?error=true"));
    }
    
    //Logout
    @Test
    public void deveFazerLogout() throws Exception {
        MvcResult login = mockMvc.perform(post("/clientes/login")
                .param("email", "cliente@teste.com")
                .param("password", "123456")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andReturn();

        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

        mockMvc.perform(post("/clientes/logout")
                .session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"));

        // Após logout, acesso deve redirecionar para login
        mockMvc.perform(get("/pedidos/clientes/meus-pedidos")
                .session(session))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrlPattern("**/clientes/login"));
    }
}

