package com.devsoft.rgdi_store.controllers;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.devsoft.rgdi_store.dto.ProdutoImagensDto;
import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.ItensPedidoEntity;
import com.devsoft.rgdi_store.entities.PedidoEntity;
import com.devsoft.rgdi_store.entities.PedidoStatus;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.exceptions.all.PedidoNaoEncontradoException;
import com.devsoft.rgdi_store.services.PedidoService;
import com.devsoft.rgdi_store.services.ProdutoImagensService;
import com.devsoft.rgdi_store.services.ProdutoService;

@Controller
public class MainController {
	
	private final PedidoService pedidoService;
	private final ProdutoService produtoService;
	private final ProdutoImagensService produtoImagensService;	
	
	public MainController(ProdutoService produtoService,
						  PedidoService pedidoService,
						  ProdutoImagensService produtoImagensService) {
		this.pedidoService = pedidoService;
		this.produtoService = produtoService;
		this.produtoImagensService = produtoImagensService;		
	}
   
	@GetMapping("/")
    public String listarProdutos(Model model) {
        // Lista de todos os produtos
        List<ProdutoEntity> produtosLoja = produtoService.findAllIndex();

        // Criar um mapa de imagens principais por produto
        Map<Long, ProdutoImagensDto> imagensPrincipais = new HashMap<>();
        for (ProdutoEntity produto : produtosLoja) {
            List<ProdutoImagensDto> imagens = produtoImagensService.buscarImagemPrincipalPorProdutoId(produto.getId());
            if (!imagens.isEmpty()) {
                imagensPrincipais.put(produto.getId(), imagens.get(0)); // Armazena a primeira imagem principal do produto
            }
        }

        // Passando os produtos e as imagens principais para a view
        model.addAttribute("produtos", produtosLoja);
        model.addAttribute("imagensPrincipais", imagensPrincipais);

        return "index"; // Nome do template Thymeleaf
    }
	
	@GetMapping("/login")
    public String loginAdmin(@RequestParam(value = "error", required = false) String error,
                               Model model) {
        if (error != null) {
            model.addAttribute("erro", "Dados inválidos. Tente novamente!");
        }
        return "login";
    }
    
    @GetMapping("/front-adm")
    public String frontAdm() {
        return "frontadm"; // Exibe a página seleção (frontadm.html)
    }
    
    @GetMapping("/logout")
    public String logoutAdmin() {
        return "login"; // Exibe a página de login (login.html)
    }
    
    @GetMapping("/admin")
    public String posLogin() {
        return "home-admin"; // Renderiza o template home-admin.html
    }    
    
    // Endpoint de autenticação
    @GetMapping("/username")
    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // Retorna o nome do usuário
    }
    
    
    // Pedidos
    @GetMapping("/pedidos/admin")
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
    
    @GetMapping("/pedidos/estoque")
    public String listarPedidosPorDataEstoque(
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
        return "pedido/listar-pedidos-estoque";
    }

    // Mostra o formulário para alterar status
    @GetMapping("/pedidos/admin/{id}")
    public String mostrarFormularioEdicao(@PathVariable Long id, Model model) {
        PedidoEntity pedido = pedidoService.findById(id)
            .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido não encontrado"));
        model.addAttribute("pedido", pedido);
        return "pedido/pedido-edit-status"; // Crie esse HTML (ou use o que você já começou)
    }
    
    // Salva a atualização do status
    @PostMapping("/pedidos/admin/atualizar")
    public String atualizarStatus(@RequestParam Long id,
                                   @RequestParam PedidoStatus grupo, // ou "status", se preferir
                                   RedirectAttributes redirectAttributes) {
        pedidoService.updateStatus(id, grupo);
        redirectAttributes.addFlashAttribute("sucesso", "Status atualizado com sucesso!");
        return "redirect:/pedidos/admin?sucesso";
    }
    
    // Método para exibir os detalhes de um pedido
    @GetMapping("/pedidos/admin/detalhes/{id}")
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

