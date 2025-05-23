package com.devsoft.rgdi_store.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.dto.ProdutoDto;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.exceptions.ResourceNotFoundException;
import com.devsoft.rgdi_store.mapper.ProdutoMapper;
import com.devsoft.rgdi_store.repositories.ProdutoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProdutoService {
   
    private final ProdutoRepository repository;
    
    public ProdutoService(ProdutoRepository repository) {
    	this.repository = repository;
    }    
    
    public List<ProdutoEntity> findAllIndex() {
        return repository.findAll(); // Aqui usamos o método findAll do repositório
    }        
    
    //Busca todos os registros - com paginação
  	@Transactional(readOnly = true)
  	public Page<ProdutoDto> findAll(Pageable pageable) {
  		if (pageable == null) {
  	        pageable = PageRequest.of(0, 10); // Página 0 com 10 itens por padrão
  	    }
  	    Page<ProdutoEntity> result = repository.findAll(pageable);
  	    // Usa o UserMapper para a conversão de UserEntity para UserDto
  	    return result.map(ProdutoMapper::toDto);
  	}
  	
  	//Busca todos os registros
  	@Transactional(readOnly = true)
  	public List<ProdutoDto> buscarTodos() {
  	    List<ProdutoEntity> result = repository.findAll();
  	    // Usa o ProdutoMapper para a conversão de ProdutoEntity para ProdutoDto
  	    return result.stream()
  	                 .map(ProdutoMapper::toDto)
  	                 .collect(Collectors.toList());
  	}

  	//Busca por nome com paginação
  	@Transactional(readOnly = true)
  	public Page<ProdutoDto> findByName(String nome, Pageable pageable) {
  	    // Define a página padrão caso 'pageable' seja nulo
  	    if (pageable == null) {
  	        pageable = PageRequest.of(0, 10);
  	    }

  	    // Verifica se o nome está vazio ou nulo e decide qual consulta executar
  	    Page<ProdutoEntity> result;
  	    if (nome == null || nome.trim().isEmpty()) {
  	        // Se o nome estiver vazio, retorna todos os usuários paginados
  	        result = repository.findAll(pageable);
  	    } else {
  	        // Se houver um nome para buscar, realiza a consulta com filtro
  	        result = repository.findByNomeContainingIgnoreCase(nome, pageable);
  	    }

  	    // Converte o resultado em UserDto usando o UserMapper
  	    return result.map(ProdutoMapper::toDto);
  	}
  	
  	//Busca por nome com paginação
  	public List<ProdutoEntity> findByNameList(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome); // Método que realiza a busca no banco
    }
  	
    // Busca por id
	@Transactional(readOnly = true)
	public ProdutoDto findById(Long id) {		
		ProdutoEntity entity = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Recurso não encontrado - service/findById [Verifique o id]"));
		return ProdutoMapper.toDto(entity); //retorna todos os campos do UserDto	
	} 
    
    //Save
  	@Transactional
  	public ProdutoDto insert(ProdutoDto dto) {		

  	    try {
  	        // Converte DTO para entidade
  	        ProdutoEntity entity = ProdutoMapper.toEntity(dto);    
  	        
  	        // Define o Status como ativo
  	        entity.setStatus(true);

  	        // Salva no banco
  	        entity = repository.saveAndFlush(entity);

  	        // Retorna DTO convertido
  	        return ProdutoMapper.toDto(entity);
  	    } catch (DataIntegrityViolationException e) {
  	        throw new ResourceNotFoundException("Erro de integridade referencial - verifique relacionamentos no banco.");
  	    }
  	}
        
    @Transactional
    public ProdutoDto update(Long id, ProdutoDto dto) {
        try {
            ProdutoEntity entity = repository.getReferenceById(id);
            ProdutoMapper.updateProductFromDto(dto, entity, repository); // Atualiza a entidade com os dados do DTO
            
            entity = repository.saveAndFlush(entity);
            
            return ProdutoMapper.toDto(entity); // Retorna DTO convertido
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado - service/update [verifique 'id'/ se está cadastrado]");
        }
    }

    //Altera o status
  	@Transactional
  	public ProdutoDto changeStatus(Long id) {
  	    try {
  	        ProdutoEntity entity = repository.getReferenceById(id);
  	        entity.setStatus(!entity.isStatus()); // Alterna o status
  	        entity = repository.saveAndFlush(entity);
  	        return ProdutoMapper.toDto(entity);
  	    } catch (EntityNotFoundException e) {
  	        throw new ResourceNotFoundException("Recurso não encontrado - service/changeStatus");
  	    }
  	}

  	
}

