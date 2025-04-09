package com.devsoft.rgdi_store.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.devsoft.rgdi_store.dto.ProdutoImagensDto;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.services.CarrinhoService;
import com.devsoft.rgdi_store.services.ProdutoImagensService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private ProdutoImagensService produtoImagensService;
    
    @Autowired
    private HttpSession session; // Injetar a sessão

    // Método para adicionar produto ao carrinho
    @PostMapping("/carrinho/adicionar")
    public String adicionarProdutoAoCarrinho(@RequestParam("produtoId") Long produtoId) {
        carrinhoService.adicionarProduto(produtoId);  // Chama o serviço para adicionar o produto
        return "redirect:/carrinho"; // Redireciona para a página do carrinho
    }

    // Método para exibir o carrinho
    @GetMapping("/carrinho")
    public String exibirCarrinho(Model model) {
        List<ProdutoEntity> itensCarrinho = carrinhoService.getItens(); // Recupera os itens do carrinho

        // Calcular o subtotal
        double subtotal = 0.0;
        for (ProdutoEntity produto : itensCarrinho) {
            subtotal += produto.getPreco() * produto.getQuantidade();  // Calcula o total para cada item
        }

        // Recuperar o valor do frete da sessão
        Double valorFrete = (Double) session.getAttribute("frete");
        if (valorFrete == null) {
            valorFrete = 0.0;
        }

        // Calcular o total final (subtotal + frete)
        double total = subtotal + valorFrete;

        // Criar um mapa de imagens principais para cada produto no carrinho
        Map<Long, ProdutoImagensDto> imagensPrincipais = new HashMap<>();
        for (ProdutoEntity produto : itensCarrinho) {
            List<ProdutoImagensDto> imagens = produtoImagensService.buscarImagemPrincipalPorProdutoId(produto.getId());
            if (!imagens.isEmpty()) {
                imagensPrincipais.put(produto.getId(), imagens.get(0)); // Adiciona a primeira imagem principal
            }
        }

        // Passa os dados para o modelo
        model.addAttribute("itensCarrinho", itensCarrinho);
        model.addAttribute("imagensPrincipais", imagensPrincipais);
        model.addAttribute("subtotal", subtotal);  // Passa o subtotal para a visão
        model.addAttribute("total", total);  // Passa o total final para a visão
        model.addAttribute("totalItens", carrinhoService.getQuantidadeTotalItens());  // Adiciona a quantidade de produtos distintos ao modelo
        model.addAttribute("frete", valorFrete);  // Passa o valor do frete para a visão

        return "carrinho";  // Retorna o template do carrinho
    }

    // Método para alterar a quantidade de um produto no carrinho
    @PostMapping("/carrinho/alterarQuantidade")
    public String alterarQuantidade(@RequestParam("produtoId") Long produtoId, @RequestParam("quantidade") String quantidade) {
        if ("increase".equals(quantidade)) {
            carrinhoService.incrementarQuantidade(produtoId);  // Incrementa a quantidade do produto
        } else if ("decrease".equals(quantidade)) {
            carrinhoService.decrementarQuantidade(produtoId);  // Decrementa a quantidade do produto
        }
        return "redirect:/carrinho";  // Redireciona para a página do carrinho
    }
        
    // Método para selecionar o frete
    @PostMapping("/carrinho/frete")
    public String selecionarFrete(@RequestParam("tipoFrete") String tipoFrete) {
        // Definir o valor do frete com base na opção selecionada
        double valorFrete = 0.0;

        switch (tipoFrete) {
            case "correios":
                valorFrete = 10.0;  // Exemplo de valor para frete municipal
                break;
            case "fedex":
                valorFrete = 20.0;  // Exemplo de valor para frete estadual
                break;
            case "loggi":
                valorFrete = 50.0;  // Exemplo de valor para frete nacional
                break;
        }

        // Armazenar o valor do frete na sessão como Double
        session.setAttribute("frete", valorFrete);

        return "redirect:/carrinho"; // Redireciona para a página do carrinho
    }
    
    // Método para remover um produto do carrinho
    @PostMapping("/carrinho/remover")
    public String removerProdutoDoCarrinho(@RequestParam("produtoId") Long produtoId) {
        carrinhoService.removerProduto(produtoId);  // Chama o serviço para remover o produto
        return "redirect:/carrinho";  // Redireciona para a página do carrinho
    }
}
