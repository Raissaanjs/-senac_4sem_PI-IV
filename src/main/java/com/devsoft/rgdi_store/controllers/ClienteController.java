package com.devsoft.rgdi_store.controllers;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.devsoft.rgdi_store.dto.AlterPassDTO;
import com.devsoft.rgdi_store.dto.ItemCarrinhoDTO;
import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.EnderecoTipo;
import com.devsoft.rgdi_store.entities.ItensPedidoEntity;
import com.devsoft.rgdi_store.entities.PagamentoTipo;
import com.devsoft.rgdi_store.entities.PedidoEntity;
import com.devsoft.rgdi_store.entities.UserGroup;
import com.devsoft.rgdi_store.exceptions.EnderecoDuplicadoException;
import com.devsoft.rgdi_store.exceptions.ResourceNotFoundException;
import com.devsoft.rgdi_store.exceptions.all.CarrinhoVazioException;
import com.devsoft.rgdi_store.exceptions.all.ClienteNaoEncontradoException;
import com.devsoft.rgdi_store.exceptions.all.ConfirmPassNullException;
import com.devsoft.rgdi_store.exceptions.all.CpfExistsException;
import com.devsoft.rgdi_store.exceptions.all.EmailDivergException;
import com.devsoft.rgdi_store.exceptions.all.EmailExistsException;
import com.devsoft.rgdi_store.exceptions.all.EstoqueInsuficienteException;
import com.devsoft.rgdi_store.exceptions.all.InvalidCpfException;
import com.devsoft.rgdi_store.exceptions.all.InvalidPassException;
import com.devsoft.rgdi_store.exceptions.all.NameValidationException;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.repositories.EnderecoRepository;
import com.devsoft.rgdi_store.services.CarrinhoService;
import com.devsoft.rgdi_store.services.ClienteService;
import com.devsoft.rgdi_store.services.EnderecoService;
import com.devsoft.rgdi_store.services.PedidoService;
import com.devsoft.rgdi_store.utility.ClienteAutenticadoHelper;
import com.devsoft.rgdi_store.validation.cliente.ClienteValidationSaveService;

import jakarta.persistence.EntityNotFoundException;
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
    private final EnderecoRepository enderecoRepository;
        
    //injeção de dependência via construtor
    public ClienteController(ClienteService clienteService, ClienteRepository repository,
    						 EnderecoService enderecoService, ClienteAutenticadoHelper clienteHelper,
    						 CarrinhoService carrinhoService, PedidoService pedidoService,
    						 EnderecoRepository enderecoRepository) {
    	this.clienteService = clienteService;
    	this.repository = repository;
    	this.enderecoService =enderecoService;
    	this.clienteHelper = clienteHelper;
    	this.carrinhoService = carrinhoService;
    	this.pedidoService = pedidoService;
    	this.enderecoRepository = enderecoRepository;
    }
    
    @GetMapping("/login")
    public String loginCliente(@RequestParam(value = "error", required = false) String error,
                               Model model) {
        if (error != null) {
            model.addAttribute("erro", "Dados inválidos. Tente novamente!");
        }
        return "login-cliente";
    }
    
    @GetMapping("/logout")
    public String logoutCliente() {
    	return "index";
    }
    
    @GetMapping("/noauth/cadastrar")
    public String cadastrarCliente(Model model) {
        model.addAttribute("cliente", new ClienteEntity());
        return "/cliente/cadcliente";
    }    

 	@GetMapping("/noauth/cadastrar-endereco/{clienteId}")
    public String mostrarFormularioEnderecos(@PathVariable Long clienteId, Model model) {
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("enderecoFaturamento", new EnderecoEntity());
        model.addAttribute("enderecoEntrega", new EnderecoEntity());
        return "cliente/cadendereco";
    }
 	
 	@PostMapping("/noauth/salvar-cliente")
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

        if (result.hasErrors()) {
            model.addAttribute("cliente", cliente); // Mantém os dados preenchidos

            // Constrói uma mensagem geral com os erros dos campos
            StringBuilder mensagemErro = new StringBuilder("");
            for (FieldError erroCampo : result.getFieldErrors()) {
                mensagemErro.append(" ").append(erroCampo.getDefaultMessage()).append(";");
            }

            model.addAttribute("erro", mensagemErro.toString());
            return "cliente/cadcliente";
        }

        // Define o grupo de usuário, caso não tenha sido atribuído
        if (cliente.getGrupo() == null) {
            cliente.setGrupo(UserGroup.ROLE_CLIENT);
        }

        try {
            ClienteEntity savedCliente = clienteService.saveClienteOnly(cliente, confirmaSenha);
            redirectAttributes.addAttribute("clienteId", savedCliente.getId());
            return "redirect:/clientes/noauth/cadastrar-endereco/{clienteId}";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao salvar cliente. Tente novamente.");
            return "cliente/cadcliente";
        }
    }
 	
 	@GetMapping("/auth/admin")
    public String posLoginCliente() {
        return "home-cliente"; // Renderiza o template home-admin.html
    }
 	
 	@GetMapping("/auth/detalhes/{id}")
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
    
    @GetMapping("/auth/detalhes-cliente")
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
   
    @GetMapping("/auth/editar")
    public String editarCliente(Model model, Principal principal) {
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        model.addAttribute("cliente", cliente);
        return "cliente/editcliente";
    }

    @PostMapping("/auth/update")
    public String updateCliente(
            @ModelAttribute("cliente") ClienteEntity cliente,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ClienteEntity clienteLogado = clienteHelper.getClienteLogado(principal.getName());

            clienteService.update(clienteLogado.getId(), cliente);

            redirectAttributes.addFlashAttribute("sucesso", "Dados atualizados com sucesso!");
            return "redirect:/clientes/auth/detalhes-cliente?sucesso";
        } catch (NameValidationException e) {
            // Captura erro de validação do nome
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/clientes/auth/editar";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado.");
            return "redirect:/clientes/login";
        }
    }

    @GetMapping("/auth/alterpass")
    public String editarClientePass(Model model, Principal principal) {

        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        model.addAttribute("cliente", cliente);
        model.addAttribute("alterarSenhaDTO", new AlterPassDTO()); // ← importante!
        return "cliente/alterpasscliente";
    }

    @PostMapping("/auth/updatepass")
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
            return "redirect:/clientes/auth/detalhes-cliente";
        } catch (InvalidPassException | ConfirmPassNullException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/clientes/auth/alterpass";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado.");
            return "redirect:/clientes/login";
        }
    }  
    
    
    //ENDEREÇO    
    // Cadastro endereço Inicial - Faturamento
    @PostMapping("/noauth/endereco-inicial/faturamento")
    public ResponseEntity<Map<String, Long>> salvarEnderecoFaturamentoInicial(
            @RequestParam("clienteId") Long clienteId,
            @ModelAttribute("enderecoFaturamento") EnderecoEntity enderecoFaturamento) {

        enderecoFaturamento.setTipo(EnderecoTipo.FATURAMENTO);

        ClienteEntity cliente = buscarClienteComEnderecos(clienteId);
        enderecoService.saveEndereco(cliente, enderecoFaturamento);

        return ResponseEntity.ok(Collections.singletonMap("clienteId", clienteId));
    }

    // Cadastro endereço Inicial - Entrega
    @PostMapping("/noauth/endereco-inicial/entrega")
    public String salvarEnderecoEntrega(@RequestParam("clienteId") Long clienteId,
                                        @ModelAttribute("enderecoEntrega") EnderecoEntity enderecoEntrega) {

        enderecoEntrega.setTipo(EnderecoTipo.ENTREGA);

        ClienteEntity cliente = buscarClienteComEnderecos(clienteId); // ou findById
        enderecoService.saveEndereco(cliente, enderecoEntrega);

        return "redirect:/clientes/login";
    }
    
    // Abre o form de cadastro para adicionar novo endereço - Em Meus Dados
    @GetMapping("/auth/endereco/meusdados/novo")
    public String novoEndereco(Model model, Principal principal) {
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        model.addAttribute("clienteId", cliente.getId());
        model.addAttribute("endereco", new EnderecoEntity()); // novo objeto limpo
        return "cliente/cadendereconovo";
    }
    
    @PostMapping("/auth/endereco/meusdados/salvar")
    public String salvarEndereco(
            @ModelAttribute("endereco") EnderecoEntity endereco,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        try {
            // Garante a associação com o cliente
            endereco.setCliente(cliente);

            // Validação de tipo: você pode validar se foi enviado corretamente
            if (endereco.getTipo() == null) {
                redirectAttributes.addFlashAttribute("erro", "Tipo de endereço é obrigatório.");
                return "redirect:/clientes/auth/endereco/meusdados/novo";
            }

            enderecoService.saveEndereco(cliente, endereco); // método que lida com ENTREGA/FATURAMENTO

            redirectAttributes.addFlashAttribute("sucesso", "Novo Endereço (Meus Dados) criado com sucesso!");
            return "redirect:/clientes/auth/detalhes-cliente?sucesso";

        } catch (EnderecoDuplicadoException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/clientes/auth/endereco/meusdados/novo";
        }
    }
    
    @GetMapping("/auth/endereco-entrega/listar")
    public String listarEnderecosEntrega(Model model, Principal principal) {
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        List<EnderecoEntity> enderecosEntrega = cliente.getEnderecos().stream()
            .filter(e -> e.getTipo() == EnderecoTipo.ENTREGA)
            .toList();

        model.addAttribute("enderecosEntrega", enderecosEntrega);

        return "pedido/pedido-endereco-entrega"; // Nome da view Thymeleaf
    }
    
    //Abre form para cadastrar novo endereço de entrega - Fase de fechamento de pedido
    @GetMapping("/auth/endereco-entrega/pedido/novo")
    public String novoEnderecoEntrega(Model model, Principal principal) {
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        // Cria um novo objeto EnderecoEntity já com tipo ENTREGA
        EnderecoEntity novoEndereco = new EnderecoEntity();
        novoEndereco.setTipo(EnderecoTipo.ENTREGA); // Define o tipo como ENTREGA

        // Adiciona o ID do cliente e o objeto EnderecoEntity ao modelo
        model.addAttribute("clienteId", cliente.getId());
        model.addAttribute("endereco", novoEndereco); // O modelo receberá o objeto novoEndereco

        return "cliente/cadendereconovoentrega"; // O template Thymeleaf que exibe o formulário
    }
    
    @PostMapping("/auth/endereco-entrega/pedido/salvar")
    public String salvarEnderecoEntrega(
            @RequestParam("clienteId") Long clienteId,
            @ModelAttribute("endereco") EnderecoEntity endereco,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Busca o cliente com os endereços (como o service espera)
        	ClienteEntity cliente = buscarClienteComEnderecos(clienteId);

            // Garante o tipo do endereço como ENTREGA
            endereco.setTipo(EnderecoTipo.ENTREGA);

            // Usa o método base do service
            enderecoService.saveEndereco(cliente, endereco);

            redirectAttributes.addFlashAttribute("sucesso", "Novo Endereço de Entrega criado com sucesso!");
            return "redirect:/clientes/auth/endereco-entrega/listar";

        } catch (ClienteNaoEncontradoException e) {
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado: " + e.getMessage());
            return "redirect:/clientes/auth/endereco-entrega/pedido/novo";

        } catch (EnderecoDuplicadoException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/clientes/auth/endereco-entrega/pedido/novo";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar endereço de entrega: " + e.getMessage());
            return "redirect:/clientes/auth/endereco-entrega/pedido/novo";
        }
    }

    
    @PostMapping("/auth/endereco/mudar-principal/{clienteId}/{novoPrincipalId}")
    public String mudarEnderecoPrincipal(@PathVariable Long clienteId,
                                         @PathVariable Long novoPrincipalId,
                                         RedirectAttributes redirectAttributes) {
        try {
            enderecoService.tornarPrincipal(clienteId, novoPrincipalId);
            redirectAttributes.addFlashAttribute("sucesso", "Endereço principal atualizado com sucesso!");
        } catch (ClienteNaoEncontradoException e) {
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("erro", "Endereço inválido: " + e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", "Erro inesperado: " + e.getMessage());
        }

        return "redirect:/clientes/auth/detalhes/" + clienteId;
    }
    
    
    //PAGAMENTO    
    // Exibe a forma de pagamento
    @GetMapping("/auth/pagamento/formaspagamento")
    public String exibirFormasPagamento(@RequestParam("enderecoId") Long enderecoId,
                                        Model model,
                                        Principal principal,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {

        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        // Recupera a lista de itens do carrinho (agora usando ItemCarrinhoDTO)
        List<ItemCarrinhoDTO> itensCarrinho = carrinhoService.getItens();  // Agora retorna uma lista de ItemCarrinhoDTO

        if (itensCarrinho == null || itensCarrinho.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Seu carrinho está vazio. Adicione itens antes de continuar.");
            return "redirect:/carrinho";
        }

        // Calcular o subtotal somando o valor total de cada item
        BigDecimal subtotal = itensCarrinho.stream()
                .map(ItemCarrinhoDTO::getValorTotal)  // Usa o método getValorTotal de ItemCarrinhoDTO
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // Soma os totais de todos os itens

        // Obter o valor do frete da sessão e garantir que seja BigDecimal
        Object freteObject = session.getAttribute("frete");
        BigDecimal frete = BigDecimal.ZERO; // Valor padrão caso o frete seja nulo ou inválido

        if (freteObject instanceof BigDecimal) {
            frete = (BigDecimal) freteObject; // Se já for BigDecimal, usamos diretamente
        } else if (freteObject instanceof Double) {
            frete = new BigDecimal((Double) freteObject); // Se for Double, convertemos para BigDecimal
        } else if (freteObject instanceof String) {
            try {
                frete = new BigDecimal((String) freteObject); // Se for String, tentamos converter para BigDecimal
            } catch (NumberFormatException e) {
                frete = BigDecimal.ZERO; // Caso a conversão falhe, assume-se que o frete é zero
            }
        }

        // Calcular o total (subtotal + frete)
        BigDecimal total = subtotal.add(frete);

        // Adicionar as informações ao modelo para a view
        model.addAttribute("enderecoId", enderecoId);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("frete", frete);
        model.addAttribute("total", total);

        return "pagamento/formaspagamento";  // Retorna a view do formulário de pagamento
    }


    
    // Escolhe a forma de pagamento
    @PostMapping("/auth/pagamento/formaspagamento/selecao")
    public String selecionarFormaPagamento(@RequestParam Long enderecoId,
                                           @RequestParam(required = false) String formaPagamento,
                                           Model model,
                                           Principal principal,
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes) {

        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());
        List<ItemCarrinhoDTO> itensCarrinho = carrinhoService.getItens(); // Ajuste aqui

        // Valida se o carrinho está vazio
        if (itensCarrinho == null || itensCarrinho.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Seu carrinho está vazio. Adicione itens antes de prosseguir.");
            return "redirect:/carrinho";
        }

        // Valida se a forma de pagamento foi selecionada
        if (formaPagamento == null || formaPagamento.isBlank()) {
            redirectAttributes.addFlashAttribute("erro", "Selecione uma forma de pagamento antes de continuar.");
            redirectAttributes.addAttribute("enderecoId", enderecoId);
            return "redirect:/clientes/auth/pagamento/formaspagamento";
        }

        // Calcular subtotal usando BigDecimal
        BigDecimal subtotal = itensCarrinho.stream()
            .map(ItemCarrinhoDTO::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal frete = (BigDecimal) session.getAttribute("frete");
        if (frete == null) frete = BigDecimal.ZERO;

        BigDecimal total = subtotal.add(frete);

        // Adicionar ao modelo para exibição na view
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("frete", frete);
        model.addAttribute("total", total);
        model.addAttribute("enderecoSelecionado", enderecoId);
        model.addAttribute("formaPagamento", formaPagamento);
        model.addAttribute("itensCarrinho", itensCarrinho); // Se for usar na view de resumo

        return "pedido/resumopedido";
    }


    // Mostra o resumo do pedido
    @PostMapping("/auth/pagamento/exibir-resumo")
    public String exibirResumoPedido(@RequestParam String formaPagamento,
                                     @RequestParam BigDecimal subtotal,
                                     @RequestParam BigDecimal frete,
                                     @RequestParam BigDecimal total,
                                     @RequestParam Long enderecoId,
                                     Model model,
                                     RedirectAttributes redirectAttributes,
                                     Principal principal) {

        try {
            // Recupera o cliente logado
            ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

            // Busca o endereço selecionado
            EnderecoEntity endereco = enderecoRepository.findById(enderecoId)
                    .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

            // Recupera os itens do carrinho (com quantidade e preço)
            List<ItemCarrinhoDTO> itensCarrinho = carrinhoService.getItens();
            if (itensCarrinho == null || itensCarrinho.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Seu carrinho está vazio. Adicione itens antes de prosseguir.");
                return "redirect:/carrinho";
            }

            // Cria um pedido temporário com os dados para exibição
            PedidoEntity pedido = new PedidoEntity();
            pedido.setTipo(PagamentoTipo.valueOf(formaPagamento));
            pedido.setFrete(frete);
            pedido.setValorTotal(total);
            pedido.setCliente(cliente);
            pedido.setEndereco(endereco);

            // Adiciona os dados ao modelo
            model.addAttribute("pedido", pedido);
            model.addAttribute("itensCarrinho", itensCarrinho);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("frete", frete);
            model.addAttribute("total", total);
            model.addAttribute("enderecoSelecionado", endereco);
            model.addAttribute("formaPagamento", formaPagamento);

            return "pedido/resumopedido";

        } catch (CarrinhoVazioException | EstoqueInsuficienteException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/carrinho";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao processar endereço: " + e.getMessage());
            return "redirect:/carrinho";
        }
    }


	// Confirma o pedido
    @PostMapping("/auth/pagamento/processar")
    public String processarPagamento(@RequestParam("formaPagamento") String formaPagamento,
                                     @RequestParam("subtotal") BigDecimal subtotal,
                                     @RequestParam("frete") BigDecimal frete,
                                     @RequestParam("total") BigDecimal total,
                                     @RequestParam("enderecoId") Long enderecoId,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {

        try {
            ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

            EnderecoEntity endereco = enderecoService.findById(enderecoId)
                    .orElseThrow(() -> new IllegalArgumentException("Endereço inválido."));

            PagamentoTipo tipoPagamento = PagamentoTipo.valueOf(formaPagamento);

            // Cria o pedido inicial
            PedidoEntity pedido = new PedidoEntity();
            pedido.setCliente(cliente);
            pedido.setEndereco(endereco);
            pedido.setFrete(frete);
            pedido.setValorTotal(total);
            pedido.setTipo(tipoPagamento);

            // Recupera os itens do carrinho com quantidade
            List<ItemCarrinhoDTO> itensCarrinho = carrinhoService.getItens();
            if (itensCarrinho == null || itensCarrinho.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Carrinho vazio. Não é possível finalizar o pedido.");
                return "redirect:/carrinho";
            }

            // Finaliza o pedido com os itens do carrinho
            PedidoEntity pedidoSalvo = pedidoService.finalizarPedido(pedido, itensCarrinho); // Certifique-se que esse método aceite o DTO
            redirectAttributes.addFlashAttribute("sucesso", "Criado com sucesso!");

         // Redireciona para a página de sucesso com o ID do pedido como parâmetro
            return "redirect:/clientes/auth/pedido-sucesso?pedidoId=" + pedidoSalvo.getId();


        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao processar pagamento: " + e.getMessage());
            return "redirect:/clientes/auth/pagamento/formaspagamento?enderecoId=" + enderecoId;
        }
    }
   
	
	//PEDIDOS
    @GetMapping("/auth/resumo-pedido")
	public String listarPedidosCliente(Model model) {
	    List<PedidoEntity> pedidos = pedidoService.findAll();
	    model.addAttribute("pedidos", pedidos);
	    return "pedido/listar-pedidos-admin"; // ex: templates/pedido/listar-admin.html
	}
    
    @GetMapping("/auth/pedido-sucesso")
    public String pedidoSucesso(@RequestParam("pedidoId") Long pedidoId,
                                Model model,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {

        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        PedidoEntity pedido = pedidoService.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        // Segurança: garante que o pedido pertence ao cliente logado
        if (!pedido.getCliente().getId().equals(cliente.getId())) {
        	
        	String nomeClienteLogado = cliente.getNome();
        	redirectAttributes.addFlashAttribute("erro", "O Pedido #" + pedidoId + " não pertence o (a) Cliente: "
        	+ nomeClienteLogado + ".");
            return "redirect:/clientes/auth/meus-pedidos";
        }

        model.addAttribute("pedidos", List.of(pedido)); // Reaproveitando a estrutura da view
        return "pedido/pedido-sucesso";
    }

	@GetMapping("/auth/meus-pedidos")
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
	@GetMapping("/auth/meus-pedidos/detalhes/{id}")
	public String detalhesPedido(@PathVariable("id") Long id, Principal principal, Model model) {
	    // Recupera o cliente logado
	    ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

	    if (cliente == null) {
	        throw new IllegalStateException("Cliente não encontrado. Certifique-se de estar logado.");
	    }

	    // Busca o pedido pelo ID
	    PedidoEntity pedido = pedidoService.findById(id)
	        .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

	    // Verifica se o pedido pertence ao cliente logado
	    if (!pedido.getCliente().equals(cliente)) {
	        throw new IllegalArgumentException("Você não tem permissão para visualizar este pedido.");
	    }

	    // Calcula o subtotal somando os valores totais dos itens
	    BigDecimal subtotal = pedido.getItensPedido().stream()
	        .map(ItensPedidoEntity::getVlTotalPedido)
	        .reduce(BigDecimal.ZERO, BigDecimal::add);

	    // Frete e total
	    BigDecimal frete = pedido.getFrete(); // deve vir do banco
	    BigDecimal total = subtotal.add(frete);

	    // Endereço selecionado (assumindo que está no Pedido)
	    EnderecoEntity endereco = pedido.getEndereco(); // ou pedido.getEnderecoEntrega()

	    // Adiciona ao modelo
	    model.addAttribute("pedido", pedido);
	    model.addAttribute("itensPedido", pedido.getItensPedido());
	    model.addAttribute("subtotal", subtotal);
	    model.addAttribute("frete", frete);
	    model.addAttribute("total", total);
	    model.addAttribute("enderecoSelecionado", endereco);

	    return "pedido/detalhes-pedido-cliente";
	}

    
    // ========== MÉTODO(S) AUXILIAR(ES) ==========
    public ClienteEntity buscarClienteComEnderecos(Long clienteId) {
        return clienteService.findByIdComEnderecos(clienteId);
    }   
   

}
