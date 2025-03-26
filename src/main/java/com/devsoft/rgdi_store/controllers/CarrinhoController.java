package com.devsoft.rgdi_store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.devsoft.rgdi_store.services.CarrinhoService;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @PostMapping("/adicionar")
    public String adicionarProduto(@RequestParam("produtoId") Long produtoId, Model model) {
        carrinhoService.adicionarProduto(produtoId);
        return "redirect:/carrinho";
    }

    @GetMapping
    public String verCarrinho(Model model) {
        model.addAttribute("itens", carrinhoService.getItens());
        return "carrinho";
    }
}
