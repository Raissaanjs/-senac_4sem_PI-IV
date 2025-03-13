package com.devsoft.rgdi_store.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.dto.ProductDto;
import com.devsoft.rgdi_store.dto.ProductMapper;
import com.devsoft.rgdi_store.entities.ProductEntity;
import com.devsoft.rgdi_store.repositories.ProductRepository;
import com.devsoft.rgdi_store.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;
    
    //Mostra todos os registros
  	@Transactional(readOnly = true)
  	public Page<ProductDto> findAll(Pageable pageable) {
  		if (pageable == null) {
  	        pageable = PageRequest.of(0, 5); // Página 0 com 10 itens por padrão
  	    }
  	    Page<ProductEntity> result = repository.findAll(pageable);
  	    // Usa o UserMapper para a conversão de UserEntity para UserDto
  	    return result.map(ProductMapper::toDto);
  	}

  	//Busca por nome com paginação
  	@Transactional(readOnly = true)
  	public Page<ProductDto> findByName(String nome, Pageable pageable) {
  	    // Define a página padrão caso 'pageable' seja nulo
  	    if (pageable == null) {
  	        pageable = PageRequest.of(0, 5);
  	    }

  	    // Verifica se o nome está vazio ou nulo e decide qual consulta executar
  	    Page<ProductEntity> result;
  	    if (nome == null || nome.trim().isEmpty()) {
  	        // Se o nome estiver vazio, retorna todos os usuários paginados
  	        result = repository.findAll(pageable);
  	    } else {
  	        // Se houver um nome para buscar, realiza a consulta com filtro
  	        result = repository.findByNomeContainingIgnoreCase(nome, pageable);
  	    }

  	    // Converte o resultado em UserDto usando o UserMapper
  	    return result.map(ProductMapper::toDto);
  	}
  	
    /// Busca por id
	@Transactional(readOnly = true)
	public ProductDto findById(Long id) {		
		ProductEntity entity = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Recurso não encontrado - service/findById [Verifique o id]"));
		return ProductMapper.toDto(entity); //retorna todos os campos do UserDto	
	} 
    
    //Save
  	@Transactional
  	public ProductDto insert(ProductDto dto) {		

  	    try {
  	        // Converte DTO para entidade
  	        ProductEntity entity = ProductMapper.toEntity(dto);	        
  	        
  	        // Define o Status como ativo
  	        entity.setStatus(true);

  	        // Salva no banco
  	        entity = repository.save(entity);

  	        // Retorna DTO convertido
  	        return ProductMapper.toDto(entity);
  	    } catch (DataIntegrityViolationException e) {
  	        throw new ResourceNotFoundException("Erro de integridade referencial - verifique relacionamentos no banco.");
  	    }
  	}
        
    @Transactional
    public ProductDto update(Long id, ProductDto dto) {
        try {
            ProductEntity entity = repository.getReferenceById(id);
            ProductMapper.updateProductFromDto(dto, entity, repository); // Atualiza a entidade com os dados do DTO
            
            entity = repository.save(entity);
            
            return ProductMapper.toDto(entity); // Retorna DTO convertido
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado - service/update [verifique 'id'/ se está cadastrado]");
        }
    }

    //Altera o status
  	@Transactional
  	public ProductDto changeStatus(Long id) {
  	    try {
  	        ProductEntity entity = repository.getReferenceById(id);
  	        entity.setStatus(!entity.isStatus()); // Alterna o status
  	        entity = repository.save(entity);
  	        return ProductMapper.toDto(entity);
  	    } catch (EntityNotFoundException e) {
  	        throw new ResourceNotFoundException("Recurso não encontrado - service/changeStatus");
  	    }
  	}	
}

