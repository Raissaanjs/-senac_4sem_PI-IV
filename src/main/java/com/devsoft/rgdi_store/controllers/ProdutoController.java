package com.devsoft.rgdi_store.controllers;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.devsoft.rgdi_store.config.UploadConfig;
import com.devsoft.rgdi_store.dto.ProductDto;
import com.devsoft.rgdi_store.dto.ProductMapper;
import com.devsoft.rgdi_store.entities.ProductEntity;
import com.devsoft.rgdi_store.entities.ProductImageEntity;
import com.devsoft.rgdi_store.services.ProductService;

@Controller
@RequestMapping("/produtos")
public class ProductController {
	
	private final Path uploadConfig;

    public ProductController(UploadConfig uploadConfig) {
    	this.uploadConfig = Paths.get(uploadConfig.getUploadDir())
                .toAbsolutePath().normalize();
    }
	
    @Autowired
    private ProductService productService;

    //Lista geral - Menu  
  	@GetMapping("/listar")
  	public String list(
  	    Model model,
  	    @PageableDefault(page = 0, size = 5, sort = "id") Pageable pageable
  	) {
  	    Page<ProductDto> dtoPage = productService.findAll(pageable);

  	    // Adiciona os resultados da página ao modelo do Thymeleaf
  	    model.addAttribute("produtos", dtoPage.getContent());
  	    model.addAttribute("page", dtoPage); // Metadados da página (como total de páginas e número atual)

  	    return "produto/listproduct"; // Template Thymeleaf
  	}
  	
  //Busca por id - para framework de Front/ Postman
  	@GetMapping("/detalhes/{id}")
  	public ResponseEntity<ProductDto> findById(@PathVariable Long id) {
  		ProductDto dto = productService.findById(id);
  		return ResponseEntity.ok(dto);
  	}
  	
  	@GetMapping("/buscar-nome")
	public String buscarPorNomeOuTodos(
	    @RequestParam(value = "nome", required = false, defaultValue = "") String nome,
	    @PageableDefault(page = 0, size = 5, sort = "id") Pageable pageable,
	    Model model) {
	    // Busca por nome ou retorna todos os usuários com paginação
	    Page<ProductDto> produtos = (nome == null || nome.trim().isEmpty()) 
	                                ? productService.findAll(pageable) 
	                                : productService.findByName(nome, pageable);

	    // Adiciona os dados ao modelo
	    model.addAttribute("produtos", produtos.getContent());
	    model.addAttribute("page", produtos);
	    model.addAttribute("nome", nome); // Preserva o termo de busca no formulário	   
	    return "produto/listproduct";
	}
  	
  	@GetMapping("/editar/{id}")
	public String editUser(@PathVariable Long id, Model model) {
	    ProductDto dto = productService.findById(id); 

	    model.addAttribute("dto", dto); // Adiciona o p ao modelo
	    
	    return "usuario/listuser"; // Retorna o template
	} 	
  	

    // Método para exibir o formulário de criação de produto
    @GetMapping("/cadastrar")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new ProductEntity());
        return "produto/cadproduct"; // Nome da página Thymeleaf
    }

    
    // Método para salvar um novo produto
    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute ProductDto productDto, RedirectAttributes redirectAttributes) {
    	// Converte ProductDto para ProductEntity usando o ProductMapper
        ProductEntity productEntity = ProductMapper.toEntity(productDto);
        
     // Salvar produto no banco de dados
        productService.insert(productDto);
        /*
        // Adicionar imagens ao produto
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    // Salvar o arquivo na pasta de uploads
                    Path path = Paths.get(((UploadConfig) uploadConfig).getUploadDir(), file.getOriginalFilename());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                    // Criar entidade de imagem e adicionar ao produto
                    ProductImageEntity imageEntity = new ProductImageEntity();
                    imageEntity.setUrl("/uploads/" + file.getOriginalFilename());
                    imageEntity.setProductEntity(productEntity);
                    productEntity.getImagens().add(imageEntity);

                } catch (IOException e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("message", "Falha ao carregar '" + file.getOriginalFilename() + "'");
                    return "redirect:/produtos/cadastrar";
                }
            }
        }

        // Atualiza no DB
        productService.insert(productDto);
        */

        redirectAttributes.addFlashAttribute("message", "Produto salvo com sucesso!");
        return "redirect:/produtos/listar";
    }
    
	  
	@PostMapping
	 public ResponseEntity<ProductDto> insert(@RequestBody ProductDto dto) {	
		dto =  productService.insert(dto);
	      URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
	      return ResponseEntity.created(uri).body(dto);
	}	
	 
	//Update
	@PutMapping("/update/{id}")
	public ResponseEntity<?> update(
	        @PathVariable Long id,
	        @RequestBody ProductDto dto,
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
	    
	    // Atualiza o usuário com os novos dados
	    dto = productService.update(id, dto);//retorna o update personalizado

	    return ResponseEntity.ok(dto);
	}
    
    //Alterna status
  	@PutMapping("/{id}/status")
  	public ResponseEntity<ProductDto> changeStatus(@PathVariable Long id) {
  	    ProductDto dto = productService.changeStatus(id);
  	    return ResponseEntity.ok(dto);
  	}	
}