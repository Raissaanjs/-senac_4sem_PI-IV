package com.devsoft.rgdi_store.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.devsoft.rgdi_store.config.UploadConfig;
import com.devsoft.rgdi_store.dto.ProdutoImagensDto;
import com.devsoft.rgdi_store.entities.ProdutoImagens;
import com.devsoft.rgdi_store.services.ProdutoImagensService;
import com.devsoft.rgdi_store.services.ProdutoService;

@Controller
@RequestMapping("/produto-imagens")
public class ProdutoImagensController {	
	
    @Autowired
    private ProdutoImagensService produtoImagensService;
    
    @Autowired
    private ProdutoService produtoService;
    
    @Autowired
    private UploadConfig uploadConfig;
    

    @GetMapping("/listar")
    public String list(Model model,
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        Page<ProdutoImagens> dtoPage = produtoImagensService.findAll(pageable);

        // Adiciona os resultados da página ao modelo do Thymeleaf
        model.addAttribute("produtosImage", dtoPage.getContent());
        model.addAttribute("page", dtoPage); // Metadados da página (como total de páginas e número atual)

        return "produto/produto-imagens/listimage"; // Template Thymeleaf
    }
    
    //Busca por id - para framework de Front/ Postman  	
    @GetMapping("/detalhes/imagens{id}")
  	public ResponseEntity<ProdutoImagensDto> findById(@PathVariable Long id) {
  		ProdutoImagensDto dto = produtoImagensService.buscaPorId(id);
  		
  		return ResponseEntity.ok(dto);
  	}
    
    // Usado para listagem das imagens no Modal de visualização
    @GetMapping("/detalhes/{id}")
    public ResponseEntity<List<ProdutoImagensDto>> buscarImagensPorProdutoId(@PathVariable Long id) {
        List<ProdutoImagensDto> imagens = produtoImagensService.buscarImagensPorProdutoId(id); // Seu serviço para buscar as imagens
        return ResponseEntity.ok(imagens);
    }
    
    // Usar para listagem da imagem do produto na Home (index.html)
    @GetMapping("/imagem-principal/{id}")
    public ResponseEntity<List<ProdutoImagensDto>> buscarImagemPrincipalPorProdutoId(@PathVariable Long id) {
        List<ProdutoImagensDto> imagemPrincipal = produtoImagensService.buscarImagemPrincipalPorProdutoId(id); // Seu serviço para buscar as imagens
        return ResponseEntity.ok(imagemPrincipal);
    }    
    
    @GetMapping("/cadastrar")
    public String showForm(Model model) {
        model.addAttribute("productImage", new ProdutoImagens());
        model.addAttribute("produtosList", produtoService.buscarTodos()); // vem da class ProdutoService/buscarTodos
        return "produto/produto-imagens/cadimage";
    }
        
    @PostMapping("/salvar")
    public String inserir(@RequestParam("produto") Long idProduto, @RequestParam("file") MultipartFile file, Model model) {
        try {
            produtoImagensService.inserir(idProduto, file);
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("produtosList", produtoService.buscarTodos());
            return "produto/produto-imagens/cadimage";
        }
        return "redirect:/produto-imagens/cadastrar";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProdutoImagens produtoImagens = produtoImagensService.findById(id);
        if (produtoImagens != null) {
            model.addAttribute("productImage", produtoImagens);
            return "/produto/produto-imagens/cadimage";
        } else {
            return "redirect:/produto-imagens";
        }
    }    

    @PutMapping("/update/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Model model) {
        try {
            // Atualiza a imagem do produto com os novos dados
            ProdutoImagens updatedProdutoImagens = produtoImagensService.update(id, file);

            return "redirect:/produto-imagens/cadastrar";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("produtosList", produtoService.buscarTodos());
            return "produto/produto-imagens/cadimage";
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteProductImage(@PathVariable Long id) {
    	produtoImagensService.deleteById(id);
        return "redirect:/produto-imagens/listar";
    }
}