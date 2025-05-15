package com.devsoft.rgdi_store.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.devsoft.rgdi_store.dto.ItemCarrinhoDTO;
import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.repositories.EnderecoRepository;
import com.devsoft.rgdi_store.repositories.UserRepository;
import com.devsoft.rgdi_store.services.CarrinhoService;
import com.devsoft.rgdi_store.services.EnderecoService;
import com.devsoft.rgdi_store.services.PedidoService;
import com.devsoft.rgdi_store.utility.ClienteAutenticadoHelper;

@WebMvcTest(
	controllers = PagamentoController.class,
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE
		)
	}
)
@MockBean(ClienteRepository.class)
@MockBean(CarrinhoService.class)
@MockBean(ClienteAutenticadoHelper.class)
@MockBean(UserRepository.class)
public class PagamentoControllerTest {	

    @Autowired
    private MockMvc mockMvc;
    private static final String EMAIL_PADRAO = "cliente@email.com";
    
    @MockBean private ClienteEntity cliente;
    @MockBean private CarrinhoService carrinhoService;
    @MockBean private PedidoService pedidoService;
    @MockBean private EnderecoService enderecoService;
    @MockBean private ClienteAutenticadoHelper clienteHelper;
    @MockBean private EnderecoRepository enderecoRepository;
   
   
    @BeforeEach
    public void setup() {
        cliente.setEmail("EMAIL_PADRAO");
        Mockito.when(clienteHelper.getClienteLogado(any())).thenReturn(cliente);
    }
    
        
    // Exibir formas de pagamento com carrinho cheio
    @Test
    public void deveExibirFormasPagamentoComCarrinhoNaoVazio() throws Exception {
        // Criando o mock do Produto
        ProdutoEntity produto = new ProdutoEntity();
        produto.setPreco(new BigDecimal("50.00"));
        
        // Criando o mock do ItemCarrinhoDTO
        ItemCarrinhoDTO item = new ItemCarrinhoDTO();
        item.setProduto(produto);  // Associando o Produto ao ItemCarrinhoDTO
        item.setQuantidade(2);  // Quantidade do item no carrinho
        
        // Calculando o valor total (preço unitário * quantidade)
        BigDecimal valorTotal = produto.getPreco().multiply(new BigDecimal(item.getQuantidade()));
        
        // Mock do carrinho para retornar o item
        Mockito.when(carrinhoService.getItens()).thenReturn(Arrays.asList(item));
        
        // Mock do frete na sessão
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("frete", new BigDecimal("20.00"));
        
        mockMvc.perform(get("/pagamentos/formaspagamento")
	            .param("enderecoId", "1")
	            .session(session)
	            .with(user("EMAIL_PADRAO").roles("CLIENT")))
            .andExpect(status().isOk())
            .andExpect(view().name("pagamento/formaspagamento"))
            .andExpect(model().attributeExists("enderecoId", "subtotal", "frete", "total"));
    }
    
    // Redirecionar se carrinho estiver vazio
    @Test
    public void deveRedirecionarQuandoCarrinhoVazio() throws Exception {
    	
        // Mock do serviço de carrinho para retornar um carrinho vazio
        Mockito.when(carrinhoService.getItens()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pagamentos/formaspagamento")
                .param("enderecoId", "1")
                .with(user("EMAIL_PADRAO").roles("CLIENT")))
            .andExpect(status().is3xxRedirection()) // Espera redirecionamento
            .andExpect(flash().attribute("erro", "Seu carrinho está vazio. Adicione itens antes de continuar.")); // Espera erro na flash
    }

}
