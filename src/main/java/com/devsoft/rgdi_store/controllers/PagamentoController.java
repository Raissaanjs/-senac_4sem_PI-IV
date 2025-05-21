package com.devsoft.rgdi_store.controllers;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.devsoft.rgdi_store.dto.ItemCarrinhoDTO;
import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.PagamentoTipo;
import com.devsoft.rgdi_store.entities.PedidoEntity;
import com.devsoft.rgdi_store.exceptions.all.CarrinhoVazioException;
import com.devsoft.rgdi_store.exceptions.all.EstoqueInsuficienteException;
import com.devsoft.rgdi_store.repositories.EnderecoRepository;
import com.devsoft.rgdi_store.services.CarrinhoService;
import com.devsoft.rgdi_store.services.EnderecoService;
import com.devsoft.rgdi_store.services.PedidoService;
import com.devsoft.rgdi_store.utility.ClienteAutenticadoHelper;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/pagamentos")
public class PagamentoController {	
	
    private final EnderecoService enderecoService;
    private final ClienteAutenticadoHelper clienteHelper;
    private final CarrinhoService carrinhoService;
    private final PedidoService pedidoService;
    private final EnderecoRepository enderecoRepository;
        
    //injeção de dependência via construtor
    public PagamentoController(EnderecoService enderecoService,
    		ClienteAutenticadoHelper clienteHelper,
    		CarrinhoService carrinhoService,
    		PedidoService pedidoService,
    		EnderecoRepository enderecoRepository) {
    	this.enderecoService =enderecoService;
    	this.clienteHelper = clienteHelper;
    	this.carrinhoService = carrinhoService;
    	this.pedidoService = pedidoService;
    	this.enderecoRepository = enderecoRepository;
    }	
	   
    // Exibe a forma de pagamento
    @GetMapping("/formaspagamento")
    public String exibirFormasPagamento(@RequestParam("enderecoId") Long enderecoId,
                                        Model model,
                                        Principal principal,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {

        clienteHelper.getClienteLogado(principal.getName());

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

    // Mostra o resumo do pedido
    @PostMapping("/exibir-resumo")
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
    @PostMapping("/processar")
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

            // Strategy
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
            return "redirect:/pedidos/clientes/pedido-sucesso?pedidoId=" + pedidoSalvo.getId();


        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao processar pagamento: " + e.getMessage());
            return "redirect:/clientes/auth/pagamento/formaspagamento?enderecoId=" + enderecoId;
        }
    }

}
