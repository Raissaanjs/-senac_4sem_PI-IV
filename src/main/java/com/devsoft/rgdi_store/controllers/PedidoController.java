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
@RequestMapping("/pedidos")
public class PedidoController {	
   
    private final ClienteAutenticadoHelper clienteHelper;
    private final PedidoService pedidoService;
    //injeção de dependência via construtor
    public PedidoController(EnderecoService enderecoService,
    						ClienteAutenticadoHelper clienteHelper,
    						PedidoService pedidoService,
    						EnderecoRepository enderecoRepository) {
    	
    	this.clienteHelper = clienteHelper;
    	this.pedidoService = pedidoService;
    }

	//Pedidos CLIENTES
    @GetMapping("/clientes/resumo-pedido")
	public String listarPedidosCliente(Model model) {
	    List<PedidoEntity> pedidos = pedidoService.findAll();
	    model.addAttribute("pedidos", pedidos);
	    return "pedido/listar-pedidos-admin"; // ex: templates/pedido/listar-admin.html
	}
    
    @GetMapping("/clientes/pedido-sucesso")
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
            return "redirect:/pedidos/clientes/meus-pedidos";
        }

        model.addAttribute("pedidos", List.of(pedido)); // Reaproveitando a estrutura da view
        return "pedido/pedido-sucesso";
    }

	@GetMapping("/clientes/meus-pedidos")
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
	@GetMapping("/clientes/meus-pedidos/detalhes/{id}")
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
	
	
	// Pedidos ADMIN
    @GetMapping("/admin")
    public String listarPedidosPorData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

    	Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("dataCompra"), Sort.Order.desc("id")));
        Page<PedidoEntity> pedidosPage;

        if (dataInicio != null && dataFim != null) {
            pedidosPage = pedidoService.buscarPorIntervaloDeDatas(dataInicio, dataFim, pageable);
        } else {
            pedidosPage = pedidoService.listarPedidosPaginados(pageable);
        }

        model.addAttribute("pedidos", pedidosPage.getContent());
        model.addAttribute("page", pedidosPage);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        return "pedido/listar-pedidos-admin";
    }    

    // Mostra o formulário para alterar status
    @GetMapping("/admin/{id}")
    public String mostrarFormularioEdicao(@PathVariable Long id, Model model) {
        PedidoEntity pedido = pedidoService.findById(id)
            .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido não encontrado"));
        model.addAttribute("pedido", pedido);
        return "pedido/pedido-edit-status"; // Crie esse HTML (ou use o que você já começou)
    }
    
    // Salva a atualização do status
    @PostMapping("/admin/atualizar")
    public String atualizarStatus(@RequestParam Long id,
                                   @RequestParam PedidoStatus grupo, // ou "status", se preferir
                                   RedirectAttributes redirectAttributes) {
        pedidoService.updateStatus(id, grupo);
        redirectAttributes.addFlashAttribute("sucesso", "Status atualizado com sucesso!");
        return "redirect:/pedidos/admin?sucesso";
    }
    
    // Método para exibir os detalhes de um pedido
    @GetMapping("/admin/detalhes/{id}")
    public String detalhesPedidoAdmin(@PathVariable("id") Long id,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        // Busca o pedido pelo ID
        PedidoEntity pedido = pedidoService.findById(id).orElse(null);
        if (pedido == null) {
            redirectAttributes.addFlashAttribute("erro", "Pedido não encontrado.");
            return "redirect:/pedidos/admin"; // Redireciona para a lista de pedidos
        }

        // Calcula o subtotal somando os valores totais dos itens
        BigDecimal subtotal = pedido.getItensPedido().stream()
            .map(ItensPedidoEntity::getVlTotalPedido)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Frete e total
        BigDecimal frete = pedido.getFrete();
        BigDecimal total = subtotal.add(frete);

        // Endereço do pedido
        EnderecoEntity endereco = pedido.getEndereco(); // ou pedido.getEnderecoEntrega()

        // Adiciona dados ao modelo
        model.addAttribute("pedido", pedido);
        model.addAttribute("itensPedido", pedido.getItensPedido());
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("frete", frete);
        model.addAttribute("total", total);
        model.addAttribute("enderecoSelecionado", endereco);

        return "pedido/detalhes-pedido-cliente-adm"; // View para exibir detalhes do pedido ao admin
    }
}
