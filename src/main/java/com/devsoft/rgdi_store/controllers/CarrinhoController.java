package com.devsoft.rgdi_store.controllers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.devsoft.rgdi_store.dto.ItemCarrinhoDTO;
import com.devsoft.rgdi_store.dto.ProdutoImagensDto;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.services.CarrinhoService;
import com.devsoft.rgdi_store.services.ProdutoImagensService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CarrinhoController {

    private final CarrinhoService carrinhoService;
    private final ProdutoImagensService produtoImagensService;
    
    //Injeção de dependência
    public CarrinhoController(CarrinhoService carrinhoService, ProdutoImagensService produtoImagensService) {
    	this.carrinhoService = carrinhoService;
    	this.produtoImagensService = produtoImagensService; 
    }    
    
    // Método para exibir o carrinho
    @GetMapping("/carrinho")
    public String exibirCarrinho(Model model, HttpSession session) {
        // Recupera os itens do carrinho (já com a quantidade e os detalhes dos produtos)
        List<ItemCarrinhoDTO> itensCarrinho = carrinhoService.getItens(); 

        // Calcular o subtotal
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemCarrinhoDTO item : itensCarrinho) {
            // O preço já está dentro do ProdutoDto dentro de ItemCarrinhoDTO
            BigDecimal precoUnitario = item.getProduto().getPreco();
            int quantidade = item.getQuantidade();

            // Calcula o total para cada item
            subtotal = subtotal.add(precoUnitario.multiply(BigDecimal.valueOf(quantidade)));
        }

        // Recuperar o valor do frete da sessão (alterado para BigDecimal)
        BigDecimal valorFrete = (BigDecimal) session.getAttribute("frete");
        if (valorFrete == null) {
            valorFrete = BigDecimal.ZERO;  // Se for nulo, atribui zero
        }

        // Calcular o total final (subtotal + frete)
        BigDecimal total = subtotal.add(valorFrete);

        // Criar um mapa de imagens principais para cada produto no carrinho
        Map<Long, ProdutoImagensDto> imagensPrincipais = new HashMap<>();
        for (ItemCarrinhoDTO item : itensCarrinho) {
            ProdutoEntity produto = item.getProduto(); // Obtém a entidade Produto
            List<ProdutoImagensDto> imagens = produtoImagensService.buscarImagemPrincipalPorProdutoId(produto.getId());
            if (!imagens.isEmpty()) {
                imagensPrincipais.put(produto.getId(), imagens.get(0)); // Adiciona a primeira imagem principal
            }
        }

        // Adiciona os dados ao Model para ser mostrado na View
        model.addAttribute("itensCarrinho", itensCarrinho);  // Passa a lista de ItemCarrinhoDTO
        model.addAttribute("imagensPrincipais", imagensPrincipais);
        model.addAttribute("subtotal", subtotal);  // Passa o subtotal para a view
        model.addAttribute("total", total);  // Passa o total final para a view
        model.addAttribute("totalItens", carrinhoService.getQuantidadeTotalItens());  // Adiciona a quantidade de produtos distintos
        model.addAttribute("frete", valorFrete);  // Passa o valor do frete para a view

        return "carrinho";  // View que retorna a página do carrinho
    }


    //Usado para verificar o carrinho ao clicar em Finalizar Compra
    @GetMapping("carrinho/verificar")
    public ResponseEntity<Map<String, Boolean>> verificarCarrinho() {
    	int quantidadeItens = carrinhoService.getQuantidadeTotalItens();
    	Map<String, Boolean> response = new HashMap<>();
    	response.put("temItens", quantidadeItens > 0);
    	return ResponseEntity.ok(response);
    }
    
    // Método para adicionar produto ao carrinho
    @PostMapping("/carrinho/adicionar")
    public String adicionarProdutoAoCarrinho(@RequestParam("produtoId") Long produtoId) {
        carrinhoService.adicionarProduto(produtoId); // Chama o serviço para adicionar o produto
        return "redirect:/"; // Redireciona para View que retorna a página inicial
    }
    
    // Método para alterar a quantidade de um produto no carrinho
    @PostMapping("/carrinho/alterarQuantidade")
    public String alterarQuantidade(@RequestParam("produtoId") Long produtoId, @RequestParam("quantidade") String quantidade) {
        if ("increase".equals(quantidade)) {
            carrinhoService.incrementarQuantidade(produtoId); // Incrementa a quantidade do produto
        } else if ("decrease".equals(quantidade)) {
            carrinhoService.decrementarQuantidade(produtoId); // Decrementa a quantidade do produto
        }
        return "redirect:/carrinho"; // Redireciona para View que retorna a página do carrinho
    }
        
    // Método para selecionar o frete
    @PostMapping("/carrinho/frete")
    public String selecionarFrete(@RequestParam("tipoFrete") String tipoFrete, HttpSession session) {
        // Definir o valor do frete com base na opção selecionada
        BigDecimal valorFrete = BigDecimal.ZERO;  // Usando BigDecimal ao invés de Double

        switch (tipoFrete) {
            case "correios":
                valorFrete = new BigDecimal("10.0");
                break;
            case "fedex":
                valorFrete = new BigDecimal("20.0");
                break;
            case "loggi":
                valorFrete = new BigDecimal("50.0");
                break;
        }

        // Armazenar o valor do frete na sessão como BigDecimal
        session.setAttribute("frete", valorFrete);

        return "redirect:/carrinho"; // Redireciona para View que retorna a página do carrinho
    }

    
    // Método para remover um produto do carrinho
    @PostMapping("/carrinho/remover")
    public String removerProdutoDoCarrinho(@RequestParam("produtoId") Long produtoId) {
        carrinhoService.removerProduto(produtoId);  // Chama o serviço para remover o produto
        return "redirect:/carrinho";  // Redireciona para View que retorna a página do carrinho
    }
    
}
