package com.devsoft.rgdi_store.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.EnderecoTipo;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.repositories.UserRepository;
import com.devsoft.rgdi_store.services.CarrinhoService;
import com.devsoft.rgdi_store.services.ClienteService;
import com.devsoft.rgdi_store.services.EnderecoService;
import com.devsoft.rgdi_store.utility.ClienteAutenticadoHelper;
import com.devsoft.rgdi_store.utility.GlobalModelControllerAdvice;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(
	controllers = EnderecoController.class,
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE
		)
	}
)
@MockBean(UserRepository.class)
@MockBean(CarrinhoService.class)
public class EnderecoControllerTest {

@Autowired
private MockMvc mockMvc;

@MockBean private ClienteService clienteService;
@MockBean private ClienteRepository clienteRepository;
@MockBean private EnderecoService enderecoService;
@MockBean private ClienteAutenticadoHelper clienteHelper;
    
@BeforeEach
public void setUp() {
    // Configura o MockMvc com o controlador e a simulação de autenticação
    mockMvc = MockMvcBuilders.standaloneSetup(new EnderecoController(
        clienteService, 
        clienteRepository, 
        enderecoService, 
        clienteHelper
    ))
    .build();
}

@Test
public void deveSalvarEnderecoFaturamentoInicial() throws Exception {
    // Simula dados do endereço
    Long clienteId = 1L;
    String rua = "Rua Teste";
    String numero = "123";

    mockMvc.perform(MockMvcRequestBuilders.post("/enderecos/noauth/endereco-inicial/faturamento")
            .param("clienteId", clienteId.toString())
            .param("enderecoFaturamento.rua", rua)
            .param("enderecoFaturamento.numero", numero)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(SecurityMockMvcRequestPostProcessors.user("cliente@email.com").roles("USER")))  // Simula um usuário autenticado
        .andExpect(status().isOk())  // Verifica o código de status
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // Verifica o tipo de conteúdo
        .andExpect(jsonPath("$.clienteId").value(clienteId));  // Verifica se o clienteId foi retornado corretamente
}

@Test
public void deveSalvarEnderecoEntregaComSucesso() throws Exception {
    // Cria um objeto de EnderecoEntity para simular o envio do formulário
    EnderecoEntity enderecoEntrega = new EnderecoEntity();
    enderecoEntrega.setLogradouro("Rua Teste de Entrega");
    enderecoEntrega.setNumero(456);  // Aqui é Integer
    enderecoEntrega.setLocalidade("Cidade Teste");
    enderecoEntrega.setUf("Estado Teste");
    enderecoEntrega.setCep("12345678");  // Aqui também é Integer
    enderecoEntrega.setTipo(EnderecoTipo.ENTREGA);  // Define o tipo do endereço como ENTREGA

    // Simula o comportamento do cliente autenticado
    ClienteEntity clienteMock = new ClienteEntity();
    clienteMock.setId(1L);
    Mockito.when(clienteHelper.getClienteLogado(Mockito.anyString())).thenReturn(clienteMock);

    // Chama o endpoint para salvar o endereço de entrega
    mockMvc.perform(post("/enderecos/auth/endereco-entrega/pedido/salvar")
            .param("clienteId", "1")  // Passa o clienteId
            .param("endereco.rua", enderecoEntrega.getLogradouro())
            .param("endereco.numero", String.valueOf(enderecoEntrega.getNumero()))  // Passa o número como String
            .param("endereco.cidade", enderecoEntrega.getLocalidade())
            .param("endereco.estado", enderecoEntrega.getUf())
            .param("endereco.cep", enderecoEntrega.getCep())  // Passa o cep como String
            .param("endereco.tipo", enderecoEntrega.getTipo().toString())
            .with(SecurityMockMvcRequestPostProcessors.user("cliente@email.com").roles("CLIENT"))
            .with(SecurityMockMvcRequestPostProcessors.csrf()))  // Adiciona CSRF
            .andExpect(status().is3xxRedirection())  // Verifica o redirecionamento após o sucesso
            .andExpect(redirectedUrl("/enderecos/auth/endereco-entrega/listar"))  // Verifica se o redirecionamento foi para a lista de endereços
            .andExpect(flash().attribute("sucesso", "Novo Endereço de Entrega criado com sucesso!"));  // Verifica se a mensagem de sucesso foi passada
}

@Test
public void deveAlterarEnderecoPrincipalComSucesso() throws Exception {
    Long clienteId = 1L;
    Long novoEnderecoId = 2L;

    Mockito.doNothing().when(enderecoService).tornarPrincipal(clienteId, novoEnderecoId);

    mockMvc.perform(post("/enderecos/auth/mudar-principal/{clienteId}/{novoPrincipalId}", clienteId, novoEnderecoId)
            .with(SecurityMockMvcRequestPostProcessors.user("cliente@email.com").roles("CLIENT")))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/clientes/auth/detalhes/" + clienteId))
            .andExpect(flash().attribute("sucesso", "Endereço principal atualizado com sucesso!"));
}
}
