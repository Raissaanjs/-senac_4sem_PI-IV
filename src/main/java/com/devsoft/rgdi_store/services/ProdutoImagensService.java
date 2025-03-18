package com.devsoft.rgdi_store.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.devsoft.rgdi_store.config.UploadConfig;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.entities.ProdutoImagens;
import com.devsoft.rgdi_store.repositories.ProdutoRepository;
import com.devsoft.rgdi_store.repositories.ProdutoImagensRepository;

@Service
public class ProdutoImagensService {
	
    @Autowired
    private ProdutoImagensRepository produtoImagensRepository;
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    private final UploadConfig uploadConfig; // A classe UploadConfig fornece o diretório de upload
    
    public ProdutoImagensService(UploadConfig uploadConfig){
        this.uploadConfig = uploadConfig;
    }

    @Transactional(readOnly = true)
    public List<ProdutoImagens> buscarTodos(){
    	return produtoImagensRepository.findAll();
    }
    
    //Com paginação
    @Transactional(readOnly = true)
    public Page<ProdutoImagens> findAll(Pageable pageable) {
        if (pageable == null) {
            pageable = PageRequest.of(0, 5); // Página 0 com 5 itens por padrão
        }
        Page<ProdutoImagens> result = produtoImagensRepository.findAll(pageable);
        return result;
    }
    
    @Transactional
    public ProdutoImagens inserir(Long idProduto, MultipartFile file) {
    	ProdutoEntity produto = produtoRepository.findById(idProduto).get();    	
    	ProdutoImagens objeto = new ProdutoImagens();
    	
    	try {
    		if(!file.isEmpty()) {
    			String nomeImagem =  String.valueOf(produto.getId()) + file.getOriginalFilename();
    			byte[] bytes= file.getBytes();
    			Path caminho = Paths.get(uploadConfig + nomeImagem);
    			Files.write(caminho, bytes);
    			objeto.setNome(nomeImagem);
    		}
    	}catch(IOException e) {
    		e.printStackTrace();
    	}
    	
    	objeto.setProduct(produto);
        return produtoImagensRepository.saveAndFlush(objeto);
    }

    @Transactional(readOnly = true)
    public ProdutoImagens findById(Long id) {
        return produtoImagensRepository.findById(id).orElse(null);
    }    
    
    @Transactional
    public ProdutoImagens alterar(ProdutoImagens objeto) {
        return produtoImagensRepository.saveAndFlush(objeto);
    } 

    @Transactional
    public void deleteById(Long id) {
        produtoImagensRepository.deleteById(id);
    }

    
}
