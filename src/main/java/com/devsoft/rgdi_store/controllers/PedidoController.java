package com.devsoft.rgdi_store.controllers;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.ItensPedidoEntity;
import com.devsoft.rgdi_store.entities.PedidoEntity;
import com.devsoft.rgdi_store.entities.PedidoStatus;
import com.devsoft.rgdi_store.exceptions.all.PedidoNaoEncontradoException;
import com.devsoft.rgdi_store.repositories.EnderecoRepository;
import com.devsoft.rgdi_store.services.EnderecoService;
import com.devsoft.rgdi_store.services.PedidoService;
import com.devsoft.rgdi_store.utility.ClienteAutenticadoHelper;

@Controller
@RequestMapping("/pedidos") // Define a URL base para todas as solicitações deste controlador
public class PedidoController {	
   
    private final ClienteAutenticadoHelper clienteHelper;
    private final PedidoService pedidoService;
    
    public PedidoController(EnderecoService enderecoService,
    						ClienteAutenticadoHelper clienteHelper,
    						PedidoService pedidoService,
    						EnderecoRepository enderecoRepository) {
    	
    	this.clienteHelper = clienteHelper;
    	this.pedidoService = pedidoService;
    }
    
    @GetMapping("/clientes/pedido-sucesso")
    public String pedidoSucesso(@RequestParam("pedidoId") Long pedidoId,
                                Model model,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {

    	// Recupera o cliente autenticado
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        // Verifica se existe pedido no DB
        PedidoEntity pedido = pedidoService.findById(pedidoId)
        		// Se não houver envia a mensagem abaixo
        		.orElseThrow(() -> new PedidoNaoEncontradoException("Pedido com ID " + pedidoId + " não encontrado."));

        // Garante que o pedido pertence ao cliente logado
        if (!pedido.getCliente().getId().equals(cliente.getId())) {
        	
        	String nomeClienteLogado = cliente.getNome();
        	// Envia mensagem abaixo para View
        	redirectAttributes.addFlashAttribute("erro", "O Pedido #" + pedidoId + " não pertence o (a) Cliente: "
        	+ nomeClienteLogado + ".");        	
        	// Redireciona para o endpoint que chama a View que retorna a página com os pedidos do cliente
        	return "redirect:/pedidos/clientes/meus-pedidos";
        }

        // Adiciona os dados ao Model para ser mostrado na View
        model.addAttribute("pedidos", List.of(pedido));
        return "pedido/pedido-sucesso"; // View que retorna a página de finalização de pedido com sucesso
    }

	@GetMapping("/clientes/meus-pedidos")
    public String meusPedidos(Principal principal, Model model) {
		// Recupera o cliente logado
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        // Se não encontrar o cliente
        if (cliente == null) {
        	 // Adiciona os dados ao Model para ser mostrado na View
        	 model.addAttribute("erro", "Cliente não encontrado. Certifique-se de estar logado.");
        	 return "pedido/listar-pedidos-cliente"; // View que retorna a página listar pedidos do cliente
        }

        // Busca os pedidos do cliente logado
        List<PedidoEntity> pedidos = pedidoService.findByCliente(cliente);

        // Adiciona os dados ao Model para ser mostrado na View
        model.addAttribute("pedidos", pedidos);

        return "pedido/listar-pedidos-cliente"; // View que retorna a página listar pedidos do cliente
    }
	
	// Método para exibir os detalhes de um pedido
	@GetMapping("/clientes/meus-pedidos/detalhes/{id}")
	public String detalhesPedido(@PathVariable("id") Long id, Principal principal, Model model) {
	    // Recupera o cliente logado
	    ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

	    // Se não encontrar o cliente
	    if (cliente == null) {
	    	// Adiciona os dados ao Model para ser mostrado na View
	    	model.addAttribute("erro", "Não foi possível identificar o cliente. Por favor, faça login novamente.");
       	 	return "pedido/listar-pedidos-cliente"; // View que retorna a página listar pedidos do cliente  
	    }

	    PedidoEntity pedido;
	    try {
	        // Busca o pedido pelo ID
	        pedido = pedidoService.findById(id)
	        		// Se não houver envia a mensagem abaixo
	                .orElseThrow(() -> new PedidoNaoEncontradoException("Ops! Não encontramos o pedido que você procurava."));
	      // Se houver erro
	    } catch (IllegalArgumentException e) {
	    	// Adiciona os dados ao Model para ser mostrado na View
	        model.addAttribute("erro", e.getMessage());
	        return "pedido/listar-pedidos-cliente"; // View que retorna a página listar pedidos do cliente 
	    }

	    // Verifica se o pedido pertence ao cliente logado
	    if (!pedido.getCliente().equals(cliente)) {
	    	// Se não pertencer Adiciona os dados ao Model para ser mostrado na View
	    	model.addAttribute("erro", "Este pedido não pertence à sua conta. Verifique os dados e tente novamente.");
       	 	return "pedido/listar-pedidos-cliente"; // View que retorna a página listar pedidos do cliente
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

	    // Adiciona os dados ao Model para ser mostrado na View
	    model.addAttribute("pedido", pedido);
	    model.addAttribute("itensPedido", pedido.getItensPedido());
	    model.addAttribute("subtotal", subtotal);
	    model.addAttribute("frete", frete);
	    model.addAttribute("total", total);
	    model.addAttribute("enderecoSelecionado", endereco);

	    return "pedido/detalhes-pedido-cliente";
	}
	
	
	// Pedidos ADMIN
    @GetMapping("/admin")
    public String listarPedidosPorData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
    	
    	// Busca pedido por intervalo de data
    	Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("dataCompra"), Sort.Order.desc("id")));
        Page<PedidoEntity> pedidosPage;

        if (dataInicio != null && dataFim != null) {
            pedidosPage = pedidoService.buscarPorIntervaloDeDatas(dataInicio, dataFim, pageable);
        } else {
            pedidosPage = pedidoService.listarPedidosPaginados(pageable);
        }

        // Adiciona os dados ao Model para ser mostrado na View
        model.addAttribute("pedidos", pedidosPage.getContent());
        model.addAttribute("page", pedidosPage);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        return "pedido/listar-pedidos-admin"; // View que retorna a página listar pedidos - ADMIN
    }    

    @GetMapping("/admin/{id}")
    public String mostrarFormularioEdicao(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Busca o pedido pelo ID
            PedidoEntity pedido = pedidoService.findById(id)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Ops! Não encontramos o pedido que você procurava."));

            // Adiciona os dados ao Model para ser mostrado na View
            model.addAttribute("pedido", pedido);
            return "pedido/pedido-edit-status"; // View que retorna a página de edição

        } catch (PedidoNaoEncontradoException ex) {
            // Adiciona mensagem de erro e redireciona
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/pedidos/admin"; // Página de listagem ou onde faz sentido voltar
        }
    }
    
    // Salva a atualização do status
    @PostMapping("/admin/atualizar")
    public String atualizarStatus(@RequestParam Long id,
                                   @RequestParam PedidoStatus grupo,
                                   RedirectAttributes redirectAttributes) {
        // Atualiza Status
    	pedidoService.updateStatus(id, grupo);
        // Envia mensagem abaixo para View
        redirectAttributes.addFlashAttribute("sucesso", "Status atualizado com sucesso!");
        return "redirect:/pedidos/admin?sucesso"; // Redireciona para View que retorna a página de pedidos - ADMIN
    }
    
    // Método para exibir os detalhes de um pedido
    @GetMapping("/admin/detalhes/{id}")
    public String detalhesPedidoAdmin(@PathVariable("id") Long id,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        // Busca o pedido pelo ID
        PedidoEntity pedido = pedidoService.findById(id).orElse(null);
        // Se não encontrar
        if (pedido == null) {
        	// Envia mensagem abaixo para View
            redirectAttributes.addFlashAttribute("erro", "Pedido não encontrado.");
            return "redirect:/pedidos/admin"; // Redireciona para View que retorna a página de pedidos - ADMIN
        }

        // Cálculo do subtotal
        BigDecimal subtotal = pedido.getItensPedido().stream() // Usa Stream API para percorrer a lista de itens
            .map(ItensPedidoEntity::getVlTotalPedido) // Soma o valor total (vlTotalPedido) de todos os itens do pedido
            .reduce(BigDecimal.ZERO, BigDecimal::add); 

        // Cálculo do total do pedido
        BigDecimal frete = pedido.getFrete(); //Obtém o valor do frete
        BigDecimal total = subtotal.add(frete); // Soma ao subtotal para calcular o total geral do pedido

        // Obtém o endereço associado ao pedido
        EnderecoEntity endereco = pedido.getEndereco();

        // Adiciona os dados ao Model para ser mostrado na View
        model.addAttribute("pedido", pedido);
        model.addAttribute("itensPedido", pedido.getItensPedido());
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("frete", frete);
        model.addAttribute("total", total);
        model.addAttribute("enderecoSelecionado", endereco);

        return "pedido/detalhes-pedido-cliente-adm"; // View que retorna a página de detalhes do pedido - ADMIN
    }
}
