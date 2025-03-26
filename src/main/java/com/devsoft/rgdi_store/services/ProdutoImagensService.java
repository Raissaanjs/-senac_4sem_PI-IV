package com.devsoft.rgdi_store.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.devsoft.rgdi_store.config.UploadConfig;
import com.devsoft.rgdi_store.dto.ProdutoImagensDto;
import com.devsoft.rgdi_store.dto.ProdutoImagensMapper;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.entities.ProdutoImagens;
import com.devsoft.rgdi_store.repositories.ProdutoRepository;
import com.devsoft.rgdi_store.services.exceptions.ResourceNotFoundException;
import com.devsoft.rgdi_store.repositories.ProdutoImagensRepository;

@Service
public class ProdutoImagensService {
	
    @Autowired
    private ProdutoImagensRepository produtoImagensRepository;
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private UploadConfig uploadConfig;
    
    public List<ProdutoImagens> findAllIndexImagens() {
        return produtoImagensRepository.findAll(); // Aqui usamos o método findAll do repositório
    }
    
    //Com paginação
    @Transactional(readOnly = true)
    public Page<ProdutoImagens> findAll(Pageable pageable) {
        if (pageable == null) {
            pageable = PageRequest.of(0, 10); // Página 0 com 10 itens por padrão
        }
        Page<ProdutoImagens> result = produtoImagensRepository.findAll(pageable);
        return result;
    } 
  
    @Transactional(readOnly = true)
    public ProdutoImagens findById(Long id) {
        return produtoImagensRepository.findById(id).orElse(null);
    }
    
    // Busca para produtoImagens
    @Transactional(readOnly = true)
    public List<ProdutoImagens> findByProdutoId(Long produtoId) {
        return produtoImagensRepository.findByProdutoId(produtoId);
    }
    
   
    // Busca por id
    @Transactional(readOnly = true)
    public ProdutoImagensDto buscaPorId(Long id) {
        ProdutoImagens entity = produtoImagensRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado - service/findById [Verifique o id]"));

        // Passa a entidade ao mapper
        return ProdutoImagensMapper.toDto(entity);
    }
    
    // Usado para listagem das imagens no Modal de visualização
    @Transactional(readOnly = true)
    public List<ProdutoImagensDto> buscarImagensPorProdutoId(Long id) {
        ProdutoEntity produto = produtoRepository.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        List<ProdutoImagens> imagens = produto.getProdutoImagens(); // Assume que Produto tem o método getProdutoImagens
        return imagens.stream()
                .map(imagem -> new ProdutoImagensDto(imagem.getNome(), imagem.isPrincipal())) // Mapeia para o DTO
                .collect(Collectors.toList());
    }
    
    
    // Usar para listagem da imagem do produto na Home (index.html)
    @Transactional(readOnly = true)
    public List<ProdutoImagensDto> buscarImagemPrincipalPorProdutoId(Long id) {
        ProdutoEntity produto = produtoRepository.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        
        List<ProdutoImagens> imagemPrincipal = produto.getProdutoImagens().stream()
                .filter(ProdutoImagens::isPrincipal) // Filtra apenas as imagens principais
                .collect(Collectors.toList());

        // Mapeia para o DTO e adiciona a URL relativa
        return imagemPrincipal.stream()
                .map(imagem -> {
                    ProdutoImagensDto dto = new ProdutoImagensDto(imagem.getNome(), imagem.isPrincipal());
                    dto.setUrl("/uploads/" + imagem.getNome());  // URL relativa
                    return dto;
                })
                .collect(Collectors.toList());
    }


    
    @Transactional
    public ProdutoImagens inserir(Long idProduto, MultipartFile file) {
        ProdutoEntity produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        ProdutoImagens objeto = new ProdutoImagens();
        objeto.setProduto(produto);

        // Verifica se o produto já possui uma imagem principal
        boolean hasPrincipalImage = produtoImagensRepository.existsByProdutoAndPrincipal(produto, true);
        if (!hasPrincipalImage) {
            objeto.setPrincipal(true); // Se não tiver imagem principal, define como principal 
        }
        
        // Primeiro, salvamos a entidade para obter o ID gerado
        ProdutoImagens savedObjeto = produtoImagensRepository.saveAndFlush(objeto);

        try {
            if (!file.isEmpty()) {
            	// Gerar nome único para a imagem
                String nomeImagem = savedObjeto.getId() + "_" + file.getOriginalFilename();
                byte[] bytes = file.getBytes();
                
                Path uploadPath = Paths.get(uploadConfig.getUploadDir()); // Obtendo o diretório configurado

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath); // Cria o diretório caso não exista
                }

                Path caminho = uploadPath.resolve(nomeImagem); // Caminho completo onde o arquivo será salvo
                Files.write(caminho, bytes); // Salva o arquivo no diretório
                savedObjeto.setNome(nomeImagem); // Definindo o nome da imagem no objeto

                // Atualizamos a entidade com o nome da imagem
                produtoImagensRepository.saveAndFlush(savedObjeto);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return savedObjeto;
    }

    @Transactional
    public ProdutoImagens update(Long id, MultipartFile file) {
        // Verifica se a imagem do produto existe
        ProdutoImagens produtoImagens = produtoImagensRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagem do produto não encontrada"));

        try {
            if (file != null && !file.isEmpty()) {
                String nomeImagem = produtoImagens.getId() + "_" + file.getOriginalFilename();
                byte[] bytes = file.getBytes();
                Path uploadPath = Paths.get(uploadConfig.getUploadDir());

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path caminho = uploadPath.resolve(nomeImagem);
                Files.write(caminho, bytes);
                produtoImagens.setNome(nomeImagem);

                // Atualiza a entidade com o nome da imagem
                produtoImagensRepository.saveAndFlush(produtoImagens);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar a imagem", e);
        }

        return produtoImagens;
    }
    
    /*
    @Transactional
    public ProdutoImagensDto update(Long id, ProdutoImagensDto dto) {
    	try {
    		ProdutoImagens entity = produtoImagensRepository.getReferenceById(id);
    		ProdutoImagensMapper.updateEntityFromDto(dto, entity); // Atualiza a entidade com os dados do DTO
            
            entity = produtoImagensRepository.saveAndFlush(entity);
            
            return ProdutoImagensMapper.toDto(entity, dto.getImagens());
    	} catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado - service/update [verifique 'id'/ se está cadastrado]");
        }        
    } 
    */

    @Transactional
    public void deleteById(Long id) {
        produtoImagensRepository.deleteById(id);
    }

    
}
