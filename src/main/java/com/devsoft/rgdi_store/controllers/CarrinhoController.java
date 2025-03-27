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

        // Criar um mapa de imagens principais para cada produto no carrinho
        Map<Long, ProdutoImagensDto> imagensPrincipais = new HashMap<>();
        for (ProdutoEntity produto : itensCarrinho) {
            // Busca as imagens principais associadas a cada produto
            List<ProdutoImagensDto> imagens = produtoImagensService.buscarImagemPrincipalPorProdutoId(produto.getId());
            if (!imagens.isEmpty()) {
                imagensPrincipais.put(produto.getId(), imagens.get(0)); // Adiciona a primeira imagem principal
            }
        }

        // Passa os produtos do carrinho e as imagens principais para o modelo
        model.addAttribute("itensCarrinho", itensCarrinho);
        model.addAttribute("imagensPrincipais", imagensPrincipais);

        // Retorna o template carrinho.html
        return "carrinho"; 
    }
}




