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

@Controller
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private ProdutoImagensService produtoImagensService;

    // Método para adicionar produto ao carrinho
    @PostMapping("/carrinho/adicionar")
    public String adicionarProdutoAoCarrinho(@RequestParam("produtoId") Long produtoId) {
        carrinhoService.adicionarProduto(produtoId);
        return "redirect:/carrinho"; // Redireciona para a página do carrinho
    }

    // Método para exibir o carrinho
    @GetMapping("/carrinho")
    public String exibirCarrinho(Model model) {
        // Recupera os itens do carrinho
        List<ProdutoEntity> itensCarrinho = carrinhoService.getItens();

        // Calcular o subtotal
        double subtotal = 0.0;
        for (ProdutoEntity produto : itensCarrinho) {
            subtotal += produto.getPreco() * produto.getQuantidade(); // Calcula o total para cada item
        }

        // Criar um mapa de imagens principais para cada produto no carrinho
        Map<Long, ProdutoImagensDto> imagensPrincipais = new HashMap<>();
        for (ProdutoEntity produto : itensCarrinho) {
            // Busca as imagens principais associadas a cada produto
            List<ProdutoImagensDto> imagens = produtoImagensService.buscarImagemPrincipalPorProdutoId(produto.getId());
            if (!imagens.isEmpty()) {
                imagensPrincipais.put(produto.getId(), imagens.get(0)); // Adiciona a primeira imagem principal
            }
        }

        // Passa os produtos do carrinho, as imagens principais e o subtotal para o modelo
        model.addAttribute("itensCarrinho", itensCarrinho);
        model.addAttribute("imagensPrincipais", imagensPrincipais);
        model.addAttribute("subtotal", subtotal);  // Passa o subtotal para a visão

        // Retorna o template carrinho.html
        return "carrinho"; 
    }

    // Método para alterar a quantidade de um produto no carrinho
    @PostMapping("/carrinho/alterarQuantidade")
    public String alterarQuantidade(@RequestParam("produtoId") Long produtoId, @RequestParam("quantidade") String quantidade) {
        if ("increase".equals(quantidade)) {
            carrinhoService.incrementarQuantidade(produtoId);  // Incrementa a quantidade
        } else if ("decrease".equals(quantidade)) {
            carrinhoService.decrementarQuantidade(produtoId);  // Decrementa a quantidade
        }
        return "redirect:/carrinho";  // Redireciona para a página do carrinho após a alteração
    }
}




