package com.devsoft.rgdi_store.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.entities.UserGroup;
import com.devsoft.rgdi_store.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

	@Autowired
    private MockMvc mockMvc;
    
	@Autowired
    private UserRepository userRepository;
    
	@Test
	@WithMockUser(username = "admin@teste.com", authorities = {"ROLE_ADMIN"})
	public void deveBuscarUsuariosPorNome() throws Exception {
	    mockMvc.perform(get("/usuarios/buscar-nome")
	            .param("nome", "João"))
	           .andExpect(status().isOk()) // Espera o status 200
	           .andExpect(view().name("usuario/listuser")) // Espera que a view seja "usuario/listuser"
	           .andExpect(model().attributeExists("usuarios")) // Espera que o modelo tenha os "usuarios"
	           .andExpect(model().attributeExists("page")) // Espera que o modelo tenha a paginação
	           .andExpect(model().attributeExists("nome")); // Espera que o modelo tenha o parâmetro "nome"
	}
	
	@Test
	public void deveNegarAcessoSemAutenticacao() throws Exception {
	    mockMvc.perform(get("/usuarios/listar"))
	           .andExpect(status().isUnauthorized()); // Espera o status 401 Unauthorized
	}
	
	@Test
    @WithMockUser(username = "admin@teste.com", authorities = {"ROLE_ADMIN"})
    public void deveVerificarEmailExistente() throws Exception {
        // Arrange: salva um usuário com o e-mail que será verificado
        UserEntity user = new UserEntity();
        user.setNome("Admin");
        user.setEmail("admin@teste.com");
        user.setSenha("123456");
        user.setGrupo(UserGroup.ROLE_ADMIN);
        userRepository.save(user); // Salva no banco de testes

        // Act + Assert: faz a requisição e espera true
        mockMvc.perform(get("/usuarios/verificar-email")
                        .param("email", "admin@teste.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true)); // Deve passar
    }
	
	@Test
    @WithMockUser(username = "admin@teste.com", authorities = {"ROLE_ADMIN"})
    public void deveCarregarPaginaCadastroUsuario() throws Exception {
        mockMvc.perform(get("/usuarios/cadastrar"))
               .andExpect(status().isOk()) // Espera um retorno 200 OK
               .andExpect(view().name("/usuario/caduser")); // Espera o nome da view
    }

	    
}
