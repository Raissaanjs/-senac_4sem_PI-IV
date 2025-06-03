package com.devsoft.rgdi_store.controllers;

import java.util.List;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.devsoft.rgdi_store.dto.ProdutoImagensDto;
import com.devsoft.rgdi_store.entities.ProdutoImagens;
import com.devsoft.rgdi_store.services.ProdutoImagensService;
import com.devsoft.rgdi_store.services.ProdutoService;

@Controller
@RequestMapping("/produto-imagens") // Define a URL base para todas as solicitações deste controlador
public class ProdutoImagensController {	
   
    private final ProdutoImagensService produtoImagensService;
    private final ProdutoService produtoService;
    
    public ProdutoImagensController(ProdutoImagensService produtoImagensService,
    								ProdutoService produtoService) {
    	this.produtoImagensService = produtoImagensService;
    	this.produtoService =produtoService; 
    }
  

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
        List<ProdutoImagensDto> imagens = produtoImagensService.buscarImagensPorProdutoId(id);
        return ResponseEntity.ok(imagens);
    }
    
    @GetMapping("/cadastrar")
    public String showForm(Model model) {
        // Prepara um novo objeto ProdutoImagens para o formulário (binding no Thymeleaf)
        model.addAttribute("productImage", new ProdutoImagens());

        // Adiciona a lista de produtos disponíveis para seleção no formulário
        model.addAttribute("produtosList", produtoService.buscarTodos());

        // Direciona para o formulário de cadastro/edição de imagem
        return "produto/produto-imagens/cadimage";
    }
        
    @PostMapping("/salvar")
    public String inserir(
            @RequestParam("produto") Long idProduto,
            @RequestParam("file") MultipartFile file,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // Insere a nova imagem do produto
            produtoImagensService.inserir(idProduto, file);

            // Mensagem de sucesso para exibir após o redirect
            redirectAttributes.addFlashAttribute("successo", "Imagem adicionada com sucesso!");

            return "redirect:/produto-imagens/cadastrar";

        } catch (RuntimeException e) {
            // Mensagem de erro para exibir na mesma view
            model.addAttribute("erro", e.getMessage());

            // Recarrega a lista de produtos para o formulário
            model.addAttribute("produtosList", produtoService.buscarTodos());

            return "produto/produto-imagens/cadimage";
        }
    }
    
    //COMPLEMENTAR
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        ProdutoImagens produtoImagens = produtoImagensService.findById(id);

        if (produtoImagens != null) {
            model.addAttribute("productImage", produtoImagens); // imagem atual do produto
            model.addAttribute("produtosList", produtoService.buscarTodos()); // lista de produtos disponíveis
            
            return "produto/produto-imagens/cadimage"; // formulário reutilizado para edição
        } else {
            redirectAttributes.addFlashAttribute("erro", "Imagem do produto não encontrada.");
            
            return "redirect:/produto-imagens/listar";
        }
    }

    //COMPLEMENTAR
	@PutMapping("/update/{id}")
	public String update(
	        @PathVariable Long id,
	        @RequestParam("file") MultipartFile file,
	        Model model,
	        RedirectAttributes redirectAttributes) {
	
	    try {
	        // Chama o service para atualizar a imagem do produto
	        ProdutoImagens updatedProdutoImagens = produtoImagensService.update(id, file);
	
	        // Mensagem de sucesso para exibir na próxima view
	        redirectAttributes.addFlashAttribute("successo", "Imagem atualizada com sucesso!");
	
	        // Redireciona para a tela de listagem ou cadastro
	        return "redirect:/produto-imagens/cadastrar";
	
	    } catch (RuntimeException e) {
	        // Adiciona a mensagem de erro ao modelo para exibir na mesma página
	        model.addAttribute("erro", e.getMessage());
	
	        // Preenche os dados necessários para renderizar a view novamente
	        model.addAttribute("produtosList", produtoService.buscarTodos());
	
	        // Retorna para a tela de cadastro de imagem com o erro
	        return "produto/produto-imagens/cadimage";
	    }
	}	
   
}