package com.devsoft.rgdi_store.controllers;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.devsoft.rgdi_store.dto.ProdutoImagensDto;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.entities.ProdutoImagens;
import com.devsoft.rgdi_store.repositories.ProdutoImagensRepository;
import com.devsoft.rgdi_store.repositories.ProdutoRepository;
import com.devsoft.rgdi_store.services.ProdutoImagensService;
import com.devsoft.rgdi_store.services.ProdutoService;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {	
	
	private final ProdutoService produtoService;
	private final ProdutoRepository produtoRepository;
	private final ProdutoImagensRepository produtoImagensRepository;
	private final ProdutoImagensService produtoImagensService;
	
	public ProdutoController(ProdutoService produtoService,
							 ProdutoRepository produtoRepository,
							 ProdutoImagensRepository produtoImagensRepository,
							 ProdutoImagensService produtoImagensService) {
		this.produtoService = produtoService;
		this.produtoRepository = produtoRepository;
		this.produtoImagensRepository = produtoImagensRepository;
		this.produtoImagensService = produtoImagensService;
	}
 
    //Lista geral - Menu  
  	@GetMapping("/listar")
  	public String list(
  	    Model model,
  	    @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable
  	) {
  	    Page<ProdutoDto> dtoPage = produtoService.findAll(pageable);

  	    // Adiciona os resultados da página ao modelo do Thymeleaf
  	    model.addAttribute("produtos", dtoPage.getContent());
  	    model.addAttribute("page", dtoPage); // Metadados da página (como total de páginas e número atual)

  	    return "produto/listproduct"; // Template Thymeleaf
  	}
  	
  	@GetMapping("/loja/{id}")
    public String exibirProduto(@PathVariable("id") Long idProduto, Model model) {
        // Busca o produto no banco de dados
        ProdutoEntity produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // Busca a imagem principal do produto
        ProdutoImagens imagemPrincipal = produtoImagensRepository.findByProdutoAndPrincipal(produto, true);

        // Adiciona os atributos ao modelo para serem usados na view
        model.addAttribute("imagemPrincipal", imagemPrincipal); // Imagem principal
        model.addAttribute("prodLoja", produto); // Produto específico (diferente de "produtos")

        // Retorna a página HTML (o nome da página .html será a view Thymeleaf)
        return "index";
    }
  	
  	//Busca por id - para framework de Front/ Postman
  	@GetMapping("/detalhes/{id}")
  	public ResponseEntity<ProdutoDto> findById(@PathVariable Long id) {
  		ProdutoDto dto = produtoService.findById(id);
  		return ResponseEntity.ok(dto);
  	}  	
  	
  	@GetMapping("/buscar-nome")
	public String buscarPorNomeOuTodos(
	    @RequestParam(value = "nome", required = false, defaultValue = "") String nome,
	    @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable,
	    Model model) {
	    // Busca por nome ou retorna todos os produtos com paginação
	    Page<ProdutoDto> produtos = (nome == null || nome.trim().isEmpty()) 
	                                ? produtoService.findAll(pageable) 
	                                : produtoService.findByName(nome, pageable);

	    // Adiciona os dados ao modelo
	    model.addAttribute("produtos", produtos.getContent());
	    model.addAttribute("page", produtos);
	    model.addAttribute("nome", nome); // Preserva o termo de busca no formulário	   
	    return "produto/listproduct";
	}  
  	
  	@GetMapping("/loja/buscar")
  	public String buscarProdutos(@RequestParam("nomeProduto") String nomeProduto, Model model) {
  	    List<ProdutoEntity> produtos = produtoService.findByNameList(nomeProduto);
  	    model.addAttribute("produtos", produtos);
  	    model.addAttribute("nomeProduto", nomeProduto);  // Garanta que o nomeProduto seja passado ao modelo

  	    // Flag para exibir mensagem se nenhum produto for encontrado
  	    if (produtos.isEmpty()) {
  	        model.addAttribute("produtoNaoEncontrado", true);
  	    }
  	    
  	    // Cria o map de imagens principais
  	    Map<Long, ProdutoImagensDto> imagensPrincipais = new HashMap<>();
  	    for (ProdutoEntity produto : produtos) {
  	        List<ProdutoImagensDto> imagens = produtoImagensService.buscarImagemPrincipalPorProdutoId(produto.getId());

  	        if (!imagens.isEmpty()) {
  	            // Considera a primeira (e normalmente única) imagem principal
  	            imagensPrincipais.put(produto.getId(), imagens.get(0));
  	        }
  	    }

  	    model.addAttribute("imagensPrincipais", imagensPrincipais);

  	    return "index";
  	}


    // Método para exibir o formulário de criação de produto
    @GetMapping("/cadastrar")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new ProdutoEntity());
        return "produto/cadproduct"; // Nome da página Thymeleaf
    }  
       
    // Método para salvar um novo produto
    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute ProdutoDto produtoDto, RedirectAttributes redirectAttributes) {
        // Salvar produto no banco de dados
        produtoService.insert(produtoDto);
        
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