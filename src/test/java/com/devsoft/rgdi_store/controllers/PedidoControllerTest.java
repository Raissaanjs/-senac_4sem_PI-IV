package com.devsoft.rgdi_store.controllers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.PedidoEntity;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.repositories.EnderecoRepository;
import com.devsoft.rgdi_store.repositories.UserRepository;
import com.devsoft.rgdi_store.services.CarrinhoService;
import com.devsoft.rgdi_store.services.EnderecoService;
import com.devsoft.rgdi_store.services.PedidoService;
import com.devsoft.rgdi_store.utility.ClienteAutenticadoHelper;

@WebMvcTest(
	controllers = PedidoController.class,
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
public class PedidoControllerTest {	

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
        
    // Listar pedidos
    @Test
    public void deveListarPedidosCliente() throws Exception {
        PedidoEntity pedido = new PedidoEntity();
        pedido.setId(123L);
        List<PedidoEntity> pedidos = List.of(pedido);
        Mockito.when(pedidoService.findByCliente(cliente)).thenReturn(pedidos);

        mockMvc.perform(get("/pedidos/clientes/meus-pedidos")
                .with(user(EMAIL_PADRAO).roles("CLIENT")))
            .andExpect(status().isOk())
            .andExpect(view().name("pedido/listar-pedidos-cliente"))
            .andExpect(model().attribute("pedidos", pedidos));
    }
    
    // Cliente autenticado
    @Test
    public void deveRetornarMeusPedidosParaClienteAutenticado() throws Exception {
        cliente.setId(1L);
        cliente.setEmail("EMAIL_PADRAO");

        // Pedido associado ao cliente
        PedidoEntity pedido = new PedidoEntity();
        pedido.setId(1L);
        pedido.setCliente(cliente);

        // Mockando dependências
        Mockito.when(clienteHelper.getClienteLogado("EMAIL_PADRAO")).thenReturn(cliente);
        Mockito.when(pedidoService.findByCliente(cliente)).thenReturn(List.of(pedido));

        // Execução da requisição e verificação da resposta
        mockMvc.perform(get("/pedidos/clientes/meus-pedidos")
                .with(user("EMAIL_PADRAO").roles("CLIENT")))
            .andExpect(status().isOk())
            .andExpect(view().name("pedido/listar-pedidos-cliente"))
            .andExpect(model().attributeExists("pedidos"));
    }

    // Cliente não autenticado
    @Test
    void deveLancarExcecaoQuandoClienteNaoAutenticado() {
        Mockito.when(clienteHelper.getClienteLogado(anyString())).thenReturn(null);

        assertThrows(Exception.class, () ->
            mockMvc.perform(get("/pedidos/clientes/meus-pedidos")
                .with(user("naoexiste@email.com").roles("CLIENT")))
        );
    }

    // Pedido pertence ao cliente
    @Test
    void deveMostrarPedidoSucessoQuandoPedidoPertenceAoCliente() throws Exception {
    	cliente.setId(1L);
    	cliente.setEmail("EMAIL_PADRAO");

    	PedidoEntity pedido = new PedidoEntity();
    	pedido.setId(1L);
    	pedido.setCliente(cliente);

    	Mockito.when(clienteHelper.getClienteLogado("EMAIL_PADRAO")).thenReturn(cliente);
    	Mockito.when(pedidoService.findById(1L)).thenReturn(Optional.of(pedido));

    	mockMvc.perform(get("/pedidos/clientes/pedido-sucesso")
    		.param("pedidoId", "1")
    		.with(user("EMAIL_PADRAO").roles("CLIENT")))
    		.andExpect(status().isOk())
    		.andExpect(view().name("pedido/pedido-sucesso"))
    		.andExpect(model().attributeExists("pedidos"));
    }
    
    // Pedido não pertence ao cliente
    @Test
    void deveRedirecionarSePedidoNaoPertencerAoCliente() throws Exception {
        // Cliente logado
        ClienteEntity clienteLogado = new ClienteEntity();
        clienteLogado.setId(1L); // <-- ID diferente do dono do pedido
        clienteLogado.setEmail("cliente@email.com");

        // Pedido de outro cliente
        ClienteEntity donoDoPedido = new ClienteEntity();
        donoDoPedido.setId(999L);
        donoDoPedido.setEmail("outro@email.com");

        PedidoEntity pedido = new PedidoEntity();
        pedido.setId(1L);
        pedido.setCliente(donoDoPedido);

        // Mock do cliente logado e pedido
        Mockito.when(clienteHelper.getClienteLogado("cliente@email.com")).thenReturn(clienteLogado);
        Mockito.when(pedidoService.findById(1L)).thenReturn(Optional.of(pedido));

        // Execução do teste
        mockMvc.perform(get("/pedidos/clientes/pedido-sucesso")
                .param("pedidoId", "1")
                .with(user("cliente@email.com").roles("CLIENT")))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/pedidos/clientes/meus-pedidos"));
    }


}
