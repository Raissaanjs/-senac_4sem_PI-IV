package com.devsoft.rgdi_store.controllers;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.devsoft.rgdi_store.dto.AlterPassDTO;
import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.EnderecoTipo;
import com.devsoft.rgdi_store.entities.PagamentoTipo;
import com.devsoft.rgdi_store.entities.PedidoEntity;
import com.devsoft.rgdi_store.entities.PedidoStatus;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.entities.UserGroup;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.services.CarrinhoService;
import com.devsoft.rgdi_store.services.ClienteService;
import com.devsoft.rgdi_store.services.EnderecoService;
import com.devsoft.rgdi_store.services.PedidoService;
import com.devsoft.rgdi_store.services.exceptions.EnderecoDuplicadoException;
import com.devsoft.rgdi_store.services.exceptions.ResourceNotFoundException;
import com.devsoft.rgdi_store.services.exceptions.All.ConfirmPassNullException;
import com.devsoft.rgdi_store.services.exceptions.All.CpfExistsException;
import com.devsoft.rgdi_store.services.exceptions.All.EmailDivergException;
import com.devsoft.rgdi_store.services.exceptions.All.EmailExistsException;
import com.devsoft.rgdi_store.services.exceptions.All.InvalidCpfException;
import com.devsoft.rgdi_store.services.exceptions.All.InvalidPassException;
import com.devsoft.rgdi_store.services.exceptions.All.NameValidationException;
import com.devsoft.rgdi_store.utility.ClienteAutenticadoHelper;
import com.devsoft.rgdi_store.validation.cliente.ClienteValidationSaveService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/clientes")
public class ClienteController {
	
    private final ClienteService clienteService;
    private final ClienteRepository repository;
    private final EnderecoService enderecoService;
    private final ClienteAutenticadoHelper clienteHelper;
    private final CarrinhoService carrinhoService;
    private final PedidoService pedidoService;
        
    //injeção de dependência via construtor
    public ClienteController(ClienteService clienteService, ClienteRepository repository,
    						 EnderecoService enderecoService, ClienteAutenticadoHelper clienteHelper,
    						 CarrinhoService carrinhoService, PedidoService pedidoService) {
    	this.clienteService = clienteService;
    	this.repository = repository;
    	this.enderecoService =enderecoService;
    	this.clienteHelper = clienteHelper;
    	this.carrinhoService = carrinhoService;
    	this.pedidoService = pedidoService;
    }
    
    @GetMapping("/login")
    public String loginCliente() {
        return "login-cliente";
    }
    
    @GetMapping("/logout")
    public String logoutCliente() {
    	return "index";
    }
    
    @GetMapping("/admin")
    public String posLoginCliente() {
        return "home-cliente"; // Renderiza o template home-admin.html
    }

    @GetMapping("/cadastrar")
    public String cadastrarCliente(Model model) {
        model.addAttribute("cliente", new ClienteEntity());
        return "/cliente/cadcliente";
    }    

 	@GetMapping("/cadastrar-endereco/{clienteId}")
    public String mostrarFormularioEnderecos(@PathVariable Long clienteId, Model model) {
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("enderecoFaturamento", new EnderecoEntity());
        model.addAttribute("enderecoEntrega", new EnderecoEntity());
        return "cliente/cadendereco";
    }	
 	
 	
 	@GetMapping("/detalhes/{id}")
    public String detalhesCliente(@PathVariable Long id, Model model) {
        ClienteEntity cliente = clienteService.findClienteById(id);
        
        // 1 endereço de faturamento (pode ser o primeiro do tipo FATURAMENTO)
        EnderecoEntity faturamento = cliente.getEnderecos().stream()
            .filter(e -> e.getTipo() == EnderecoTipo.FATURAMENTO)
            .findFirst()
            .orElse(null);

        // Vários de entrega
        List<EnderecoEntity> entregas = cliente.getEnderecos().stream()
            .filter(e -> e.getTipo() == EnderecoTipo.ENTREGA)
            .toList();

        // Adiciona ao modelo para o Thymeleaf usar
        model.addAttribute("cliente", cliente);
        model.addAttribute("enderecoFaturamento", faturamento);
        model.addAttribute("enderecosEntrega", entregas);
        return "cliente/detalhes"; // Retorna uma página de detalhes do cliente
    }
    
    @GetMapping("/detalhes-cliente")
    public String detalhesClienteLogado(Principal principal, Model model) {
    	ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        // 1 endereço de faturamento (pode ser o primeiro do tipo FATURAMENTO)
        EnderecoEntity faturamento = cliente.getEnderecos().stream()
            .filter(e -> e.getTipo() == EnderecoTipo.FATURAMENTO)
            .findFirst()
            .orElse(null);

        // Vários de entrega
        List<EnderecoEntity> entregas = cliente.getEnderecos().stream()
            .filter(e -> e.getTipo() == EnderecoTipo.ENTREGA)
            .toList();

        // Adiciona ao modelo para o Thymeleaf usar
        model.addAttribute("cliente", cliente);
        model.addAttribute("enderecoFaturamento", faturamento);
        model.addAttribute("enderecosEntrega", entregas);
        return "cliente/detalhes"; // retorna uma página de detalhes do cliente
    }
     	
 	@PostMapping("/salvar-cliente")
    public String salvarCliente(@ModelAttribute("cliente") ClienteEntity cliente,
                                @RequestParam("confirmaSenha") String confirmaSenha,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            // Valida os campos do cliente, incluindo a confirmação da senha
            ClienteValidationSaveService.validateCliente(cliente, repository, confirmaSenha);
        } catch (NameValidationException e) {
            result.rejectValue("nome", "error.nome", e.getMessage());
        } catch (InvalidCpfException | CpfExistsException e) {
            result.rejectValue("cpf", "error.cpf", e.getMessage());
        } catch (EmailDivergException | EmailExistsException e) {
            result.rejectValue("email", "error.email", e.getMessage());
        } catch (InvalidPassException | ConfirmPassNullException e) {
            result.rejectValue("senha", "error.senha", e.getMessage());
            model.addAttribute("confirmaSenhaError", e.getMessage());
        }

        // Se houver erros de validação, retorna ao formulário
        if (result.hasErrors()) {
            model.addAttribute("cliente", cliente);
            return "cliente/cadcliente";
        }

        // Define o grupo de usuário, caso não tenha sido atribuído
        if (cliente.getGrupo() == null) {
            cliente.setGrupo(UserGroup.ROLE_CLIENT);
        }

        try {
            ClienteEntity savedCliente = clienteService.saveClienteOnly(cliente, confirmaSenha);
            redirectAttributes.addAttribute("clienteId", savedCliente.getId());
            return "redirect:/clientes/cadastrar-endereco/{clienteId}";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao salvar cliente. Tente novamente.");
            return "cliente/cadcliente";
        }
    }    
   
    @GetMapping("/editar")
    public String editarCliente(Model model, Principal principal) {
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        model.addAttribute("cliente", cliente);
        return "cliente/editcliente";
    }

    @PostMapping("/update")
    public String updateCliente(
            @ModelAttribute("cliente") ClienteEntity cliente,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ClienteEntity clienteLogado = clienteHelper.getClienteLogado(principal.getName());

            clienteService.update(clienteLogado.getId(), cliente);

            redirectAttributes.addFlashAttribute("sucesso", "Dados atualizados com sucesso!");
            return "redirect:/clientes/detalhes-cliente?sucesso";
        } catch (NameValidationException e) {
            // Captura erro de validação do nome
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/clientes/editar";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado.");
            return "redirect:/clientes/login";
        }
    }

    @GetMapping("/alterpass")
    public String editarClientePass(Model model, Principal principal) {

        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        model.addAttribute("cliente", cliente);
        model.addAttribute("alterarSenhaDTO", new AlterPassDTO()); // ← importante!
        return "cliente/alterpasscliente";
    }

    @PostMapping("/updatepass")
    public String updateClientePass(
            @ModelAttribute("alterarSenhaDTO") AlterPassDTO alterPassDTO,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {

            ClienteEntity clienteLogado = clienteHelper.getClienteLogado(principal.getName());

            clienteService.alterPassword(
                clienteLogado.getId(),
                alterPassDTO.getSenhaAtual(),
                alterPassDTO.getNovaSenha(),
                alterPassDTO.getConfirmaSenha()
            );

            redirectAttributes.addFlashAttribute("sucesso", "Senha atualizada com sucesso!");
            return "redirect:/clientes/detalhes-cliente";
        } catch (InvalidPassException | ConfirmPassNullException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/clientes/alterpass";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado.");
            return "redirect:/clientes/login";
        }
    }  
    
    
    //ENDEREÇO
    @GetMapping("/enderecos/novo")
    public String novoEndereco(Model model, Principal principal) {
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        // Cria um novo objeto EnderecoEntity e adiciona ao modelo
        EnderecoEntity novoEndereco = new EnderecoEntity();
        model.addAttribute("clienteId", cliente.getId());  // Adiciona o ID do cliente ao modelo
        model.addAttribute("endereco", novoEndereco);  // Adiciona o objeto EnderecoEntity vazio ao modelo

        // Retorna o nome do template para o Thymeleaf renderizar o formulário
        return "cliente/cadendereconovo"; // O template Thymeleaf que exibe o formulário
    }
    
    @PostMapping("/salvar-endereco-faturamento-inicial")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> salvarEnderecoFaturamentoInicial(@RequestParam("clienteId") Long clienteId,
                                                                                  @ModelAttribute("enderecoFaturamento") EnderecoEntity enderecoFaturamento) {
        enderecoFaturamento.setTipo(EnderecoTipo.FATURAMENTO); // Garante que o tipo esteja setado
        clienteService.saveEndereco(clienteId, enderecoFaturamento);
        Map<String, Long> response = new HashMap<>();
        response.put("clienteId", clienteId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/salvar-endereco-entrega")
    public String salvarEnderecoEntrega(@RequestParam("clienteId") Long clienteId,
                                        @ModelAttribute("enderecoEntrega") EnderecoEntity enderecoEntrega) {
        enderecoEntrega.setTipo(EnderecoTipo.ENTREGA); // Garante que o tipo esteja setado
        clienteService.saveEndereco(clienteId, enderecoEntrega);
        //return "redirect:/clientes/detalhes/" + clienteId;
        return "redirect:/clientes/login";
    }    
    
    @PostMapping("/endereco/mudar-principal/{clienteId}/{novoPrincipalId}")
    public String mudarEnderecoPrincipal(@PathVariable Long clienteId, @PathVariable Long novoPrincipalId) {
        enderecoService.tornarPrincipal(clienteId, novoPrincipalId);
        return "redirect:/clientes/detalhes/" + clienteId;
    }

    // Adiciona novo endereço
    @PostMapping("/enderecos/salvar")
    public String salvarEndereco(
            @ModelAttribute("endereco") EnderecoEntity endereco,
            Principal principal, Model model
    ) {
    	ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        // Salva o endereço (a lógica FATURAMENTO já está tratada no service)
        try {
            enderecoService.saveEndereco(cliente, endereco);
            return "redirect:/clientes/detalhes-cliente?sucesso";
        } catch (EnderecoDuplicadoException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("endereco", endereco); // mantém os dados preenchidos no formulário
            model.addAttribute("clienteId", cliente.getId()); // necessário pro <input hidden>
            return "cliente/cadendereconovo"; // ajuste conforme o nome real da sua view
        }
    }
    
    
    //PAGAMENTO
    // Método para exibir a página de pagamento
    @GetMapping("/pagamento-resumo")
    public String exibirPagamento(Model model, Principal principal, HttpSession session) {
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        List<EnderecoEntity> entregas = cliente.getEnderecos().stream()
            .filter(e -> e.getTipo() == EnderecoTipo.ENTREGA)
            .toList();

        // Endereço de faturamento (considerado principal)
        EnderecoEntity enderecoFaturamento = cliente.getEnderecos().stream()
            .filter(e -> e.getTipo() == EnderecoTipo.FATURAMENTO)
            .findFirst()
            .orElse(null);

        List<ProdutoEntity> itensCarrinho = carrinhoService.getItens();
        double subtotal = itensCarrinho.stream()
            .mapToDouble(p -> p.getPreco() * p.getQuantidade())
            .sum();

        Double valorFrete = (Double) session.getAttribute("frete");
        if (valorFrete == null) valorFrete = 0.0;
        double total = subtotal + valorFrete;

        model.addAttribute("itensCarrinho", itensCarrinho);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("total", total);
        model.addAttribute("frete", valorFrete);
        model.addAttribute("enderecosEntrega", entregas);
        model.addAttribute("enderecoSelecionado", 
            enderecoFaturamento != null ? enderecoFaturamento.getId() : null);

        return "pagamento/resumo-formapagamento";
    }
	
    @PostMapping("/pagamento-processar")
    public String processarPagamento(@RequestParam("formaPagamento") String formaPagamento,
                                     @RequestParam("subtotal") Double subtotal,
                                     @RequestParam("frete") BigDecimal frete,
                                     @RequestParam("total") BigDecimal total,
                                     @RequestParam("enderecoId") Long enderecoId,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {

        List<ProdutoEntity> itensCarrinho = carrinhoService.getItens();

        if (itensCarrinho == null || itensCarrinho.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Sua sessão expirou. Por favor, revise seu carrinho antes de finalizar o pedido.");
            return "redirect:/carrinho";
        }

        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());
        EnderecoEntity endereco = enderecoService.findById(enderecoId)
            .orElseThrow(() -> new IllegalArgumentException("Endereço inválido."));

        PagamentoTipo tipoPagamento = PagamentoTipo.valueOf(formaPagamento);

        PedidoEntity pedido = new PedidoEntity();
        pedido.setCliente(cliente);
        pedido.setEndereco(endereco);
        pedido.setFrete(frete);
        pedido.setTipo(tipoPagamento);
        pedido.setStatus(PedidoStatus.PENDENTE_PAGAMENTO);

        // Cria o pedido com os itens do carrinho
        PedidoEntity pedidoSalvo = pedidoService.criarPedidoComItens(pedido);

        model.addAttribute("pedido", pedidoSalvo);
        model.addAttribute("itensPedido", pedidoSalvo.getItensPedido());

        return "pagamento/sucesso";
    }


	
	//PEDIDOS
	@GetMapping("/pedidos/admin")
	public String listarPedidosAdmin(Model model) {
	    List<PedidoEntity> pedidos = pedidoService.findAll();
	    model.addAttribute("pedidos", pedidos);
	    return "pedido/listar-pedidos-admin"; // ex: templates/pedido/listar-admin.html
	}

	@GetMapping("/meus-pedidos")
    public String meusPedidos(Principal principal, Model model) {
		// Recupera o cliente logado usando o clienteHelper
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        if (cliente == null) {
            // Caso o cliente não seja encontrado (por exemplo, se o cliente não está logado)
            throw new IllegalStateException("Cliente não encontrado. Certifique-se de estar logado.");
        }

        // Busca os pedidos do cliente logado
        List<PedidoEntity> pedidos = pedidoService.findByCliente(cliente);

        // Adiciona os pedidos ao modelo para serem exibidos na view
        model.addAttribute("pedidos", pedidos);

        return "pedido/listar-pedidos-cliente"; // Nome do template Thymeleaf
    }
	
	// Método para exibir os detalhes de um pedido
    @GetMapping("/meus-pedidos/detalhes/{id}")
    public String detalhesPedido(@PathVariable("id") Long id, Principal principal, Model model) {
        // Recupera o cliente logado usando o clienteHelper
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        if (cliente == null) {
            throw new IllegalStateException("Cliente não encontrado. Certifique-se de estar logado.");
        }

        // Busca o pedido pelo ID
        PedidoEntity pedido = pedidoService.findById(id).orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        // Verifica se o pedido pertence ao cliente logado
        if (!pedido.getCliente().equals(cliente)) {
            throw new IllegalArgumentException("Você não tem permissão para visualizar este pedido.");
        }

        // Adiciona os dados do pedido ao modelo
        model.addAttribute("pedido", pedido);
        model.addAttribute("itensPedido", pedido.getItensPedido());  // Itens do pedido

        return "pedido/detalhes-pedido-cliente";  // Nome do template Thymeleaf
    }

    
}
