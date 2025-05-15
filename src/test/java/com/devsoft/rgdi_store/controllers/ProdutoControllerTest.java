package com.devsoft.rgdi_store.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.devsoft.rgdi_store.dto.ProdutoDto;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.entities.ProdutoImagens;
import com.devsoft.rgdi_store.repositories.ProdutoImagensRepository;
import com.devsoft.rgdi_store.repositories.ProdutoRepository;
import com.devsoft.rgdi_store.services.ProdutoService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean private ProdutoRepository produtoRepository;    
    @MockBean private ProdutoImagensRepository produtoImagensRepository;
    @MockBean private ProdutoService produtoService;

    @Test
    @WithMockUser(username = "admin@teste.com", authorities = {"ROLE_ADMIN"})
    public void deveListarProdutosComSucesso() throws Exception {
        // Simula uma página de produtos com 1 produto fictício
        ProdutoDto produto = new ProdutoDto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setPreco(BigDecimal.valueOf(100.0));

        Page<ProdutoDto> page = new PageImpl<>(List.of(produto));

        Mockito.when(produtoService.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/produtos/listar"))
                .andExpect(status().isOk())
                .andExpect(view().name("produto/listproduct"))
                .andExpect(model().attributeExists("produtos"))
                .andExpect(model().attributeExists("page"));
    }     
    
    @Test
    public void deveExibirProdutoNaLojaComSucesso() throws Exception {
        ProdutoEntity produto = new ProdutoEntity();
        produto.setId(1L);
        produto.setNome("Camiseta");
        
        ProdutoImagens imagem = new ProdutoImagens();
        imagem.setId(1L);
        imagem.setNome("imagem.jpg");

        Mockito.when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        Mockito.when(produtoImagensRepository.findByProdutoAndPrincipal(produto, true)).thenReturn(imagem);

        mockMvc.perform(get("/produtos/loja/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attributeExists("prodLoja"))
            .andExpect(model().attributeExists("imagemPrincipal"));
    }
    
    @Test
    public void deveNegarAcessoSemAutenticacao() throws Exception {
        // Acessa a URL sem estar autenticado
        mockMvc.perform(get("/produtos/listar"))
               .andExpect(status().isUnauthorized());  // Espera o status 401 Unauthorized
    }
    
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void deveInserirProdutoComSucesso() throws Exception {
        ProdutoDto produto = new ProdutoDto();
        produto.setId(1L);
        produto.setNome("Novo Produto");
        produto.setPreco(BigDecimal.valueOf(99.99));

        Mockito.when(produtoService.insert(any())).thenReturn(produto);

        mockMvc.perform(post("/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nome": "Novo Produto",
                      "preco": 99.99
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nome").value("Novo Produto"));
    }

}

