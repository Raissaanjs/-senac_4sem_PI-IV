package com.devsoft.rgdi_store.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.devsoft.rgdi_store.config.UploadConfig;
import com.devsoft.rgdi_store.dto.ProdutoImagensDto;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.entities.ProdutoImagens;
import com.devsoft.rgdi_store.exceptions.ResourceNotFoundException;
import com.devsoft.rgdi_store.exceptions.all.UploadFalhouException;
import com.devsoft.rgdi_store.mapper.ProdutoImagensMapper;
import com.devsoft.rgdi_store.repositories.ProdutoRepository;
import com.devsoft.rgdi_store.repositories.ProdutoImagensRepository;

@Service
public class ProdutoImagensService {
	
    private final ProdutoImagensRepository produtoImagensRepository;
    private final ProdutoRepository produtoRepository;
    private final UploadConfig uploadConfig;

    public ProdutoImagensService(ProdutoImagensRepository produtoImagensRepository,
                                 ProdutoRepository produtoRepository,
                                 UploadConfig uploadConfig) {
        this.produtoImagensRepository = produtoImagensRepository;
        this.produtoRepository = produtoRepository;
        this.uploadConfig = uploadConfig;
    }

    public List<ProdutoImagens> findAllIndexImagens() {
        return produtoImagensRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<ProdutoImagens> findAll(Pageable pageable) {
        if (pageable == null) {
            pageable = PageRequest.of(0, 10);
        }
        return produtoImagensRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public ProdutoImagens findById(Long id) {
        return produtoImagensRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ProdutoImagens> findByProdutoId(Long produtoId) {
        return produtoImagensRepository.findByProdutoId(produtoId);
    }

    @Transactional(readOnly = true)
    public ProdutoImagensDto buscaPorId(Long id) {
        ProdutoImagens entity = produtoImagensRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado - service/findById [Verifique o id]"));
        return ProdutoImagensMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<ProdutoImagensDto> buscarImagensPorProdutoId(Long id) {
        ProdutoEntity produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        
        List<ProdutoImagens> imagens = produto.getProduto_imagens();
        return imagens.stream()
                .map(imagem -> new ProdutoImagensDto(imagem.getNome(), imagem.isPrincipal()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProdutoImagensDto> buscarImagemPrincipalPorProdutoId(Long id) {
        ProdutoEntity produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        
        List<ProdutoImagens> imagemPrincipal = produto.getProduto_imagens().stream()
                .filter(ProdutoImagens::isPrincipal)
                .collect(Collectors.toList());

        return imagemPrincipal.stream()
                .map(imagem -> {
                    ProdutoImagensDto dto = new ProdutoImagensDto(imagem.getNome(), imagem.isPrincipal());
                    dto.setUrl(imagem.getUrl());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ProdutoImagens inserir(Long idProduto, MultipartFile file) {
        ProdutoEntity produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        ProdutoImagens objeto = new ProdutoImagens();
        objeto.setProduto(produto);

        boolean hasPrincipalImage = produtoImagensRepository.existsByProdutoAndPrincipal(produto, true);
        if (!hasPrincipalImage) {
            objeto.setPrincipal(true);
        }

        ProdutoImagens savedObjeto = produtoImagensRepository.saveAndFlush(objeto);

        try {
            if (!file.isEmpty()) {
                String nomeImagem = savedObjeto.getId() + "_" + file.getOriginalFilename();

                // Reutilizando método privado para salvar a imagem no disco
                salvarImagemNoDisco(file, nomeImagem);

                savedObjeto.setNome(nomeImagem);
                savedObjeto.setUrl("/uploads/" + nomeImagem);
                produtoImagensRepository.saveAndFlush(savedObjeto);
            }
        } catch (IOException e) {
            // Melhor tratamento de erro: lança exceção específica
            throw new UploadFalhouException("Erro ao salvar imagem do produto", e);
        }

        return savedObjeto;
    }

    @Transactional
    public ProdutoImagens update(Long id, MultipartFile file) {
        ProdutoImagens produtoImagens = produtoImagensRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Imagem do produto não encontrada"));

        try {
            if (file != null && !file.isEmpty()) {
                String nomeImagem = produtoImagens.getId() + "_" + file.getOriginalFilename();
                
                salvarImagemNoDisco(file, nomeImagem);

                produtoImagens.setNome(nomeImagem);
                produtoImagensRepository.saveAndFlush(produtoImagens);
            }
        } catch (IOException e) {
            throw new UploadFalhouException("Erro ao atualizar imagem do produto", e);
        }

        return produtoImagens;
    }

    // Reutilização da lógica de upload de imagem
    private void salvarImagemNoDisco(MultipartFile file, String nomeArquivo) throws IOException {
        Path uploadPath = Paths.get(uploadConfig.getUploadDir());

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path caminho = uploadPath.resolve(nomeArquivo);
        Files.write(caminho, file.getBytes());
    }
}

