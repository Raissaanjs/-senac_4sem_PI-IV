package com.devsoft.rgdi_store.controllers;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.devsoft.rgdi_store.dto.ProdutoDto;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.services.ProdutoService;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {
	
	
	
    @Autowired
    private ProdutoService produtoService;

    //Lista geral - Menu  
  	@GetMapping("/listar")
  	public String list(
  	    Model model,
  	    @PageableDefault(page = 0, size = 5, sort = "id") Pageable pageable
  	) {
  	    Page<ProdutoDto> dtoPage = produtoService.findAll(pageable);

  	    // Adiciona os resultados da página ao modelo do Thymeleaf
  	    model.addAttribute("produtos", dtoPage.getContent());
  	    model.addAttribute("page", dtoPage); // Metadados da página (como total de páginas e número atual)

  	    return "produto/listproduct"; // Template Thymeleaf
  	}
  	
  	
  	//Busca por id - para framework de Front/ Postman
  	@GetMapping("/detalhes/{id}")
  	public ResponseEntity<ProdutoDto> findById(@PathVariable Long id) {
  		ProdutoDto dto = produtoService.findById(id);
  		return ResponseEntity.ok(dto);
  	}
  	
  	// Botão Visualizar - Listar produtos
  	@GetMapping("/produtoview/{id}")
  	 public String getProduto(@PathVariable Long id, Model model) {
        ProdutoDto produto = produtoService.findById(id);
        model.addAttribute("produto", produto);
        return "produto";
    }
  	
  	@GetMapping("/buscar-nome")
	public String buscarPorNomeOuTodos(
	    @RequestParam(value = "nome", required = false, defaultValue = "") String nome,
	    @PageableDefault(page = 0, size = 5, sort = "id") Pageable pageable,
	    Model model) {
	    // Busca por nome ou retorna todos os usuários com paginação
	    Page<ProdutoDto> produtos = (nome == null || nome.trim().isEmpty()) 
	                                ? produtoService.findAll(pageable) 
	                                : produtoService.findByName(nome, pageable);

	    // Adiciona os dados ao modelo
	    model.addAttribute("produtos", produtos.getContent());
	    model.addAttribute("page", produtos);
	    model.addAttribute("nome", nome); // Preserva o termo de busca no formulário	   
	    return "produto/listproduct";
	}
  	
  	
  	
  	@GetMapping("/editar/{id}")
	public String editUser(@PathVariable Long id, Model model) {
	    ProdutoDto dto = produtoService.findById(id); 

	    model.addAttribute("dto", dto); // Adiciona ao modelo
	    
	    return "usuario/listuser"; // Retorna o template
	} 	
  	

    // Método para exibir o formulário de criação de produto
    @GetMapping("/cadastrar")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new ProdutoEntity());
        return "produto/cadproduct"; // Nome da página Thymeleaf
    }
    
    // Método para exibir o formulário de criação de produto
    @GetMapping("/alterarProd")
    public String showEditForm(Model model) {
        return "produto/cadproductedit"; // Nome da página Thymeleaf
    }

    
    // Método para salvar um novo produto
    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute ProdutoDto produtoDto, RedirectAttributes redirectAttributes) {
        // Salvar produto no banco de dados
        produtoService.insert(produtoDto);       

        //redirectAttributes.addFlashAttribute("message", "Produto salvo com sucesso!");
        return "redirect:/produtos/listar";
    }
    
	  
	@PostMapping
	 public ResponseEntity<ProdutoDto> insert(@RequestBody ProdutoDto dto) {	
		dto =  produtoService.insert(dto);
	      URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
	      return ResponseEntity.created(uri).body(dto);
	}	
	 
	//Update
	@PutMapping("/update/{id}")
	public ResponseEntity<?> update(
	        @PathVariable Long id,
	        @RequestBody ProdutoDto dto,
	        BindingResult result) {
	    
	    // Verifica se há erros de validação
	    if (result.hasErrors()) {
	        Map<String, String> errors = result.getFieldErrors()
	            .stream()
	            .collect(Collectors.toMap(
	                FieldError::getField,
	                FieldError::getDefaultMessage
	            ));
	        return ResponseEntity.badRequest().body(errors);
	    }
	    
	    // Atualiza o produto com os novos dados
	    dto = produtoService.update(id, dto);//retorna o update personalizado

	    return ResponseEntity.ok(dto);
	}
    
    //Alterna status
  	@PutMapping("/{id}/status")
  	public ResponseEntity<ProdutoDto> changeStatus(@PathVariable Long id) {
  	    ProdutoDto dto = produtoService.changeStatus(id);
  	    return ResponseEntity.ok(dto);
  	}	
}