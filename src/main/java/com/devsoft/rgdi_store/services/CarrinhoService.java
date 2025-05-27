package com.devsoft.rgdi_store.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.dto.ItemCarrinhoDTO;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.exceptions.all.ProdutoNaoEncontradoException;
import com.devsoft.rgdi_store.repositories.ProdutoRepository;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;

@Service
public class CarrinhoService {

    private final ProdutoRepository produtoRepository;
    private final HttpSession session;

    public CarrinhoService(ProdutoRepository produtoRepository,
    						HttpSession session) {
        this.produtoRepository = produtoRepository;
        this.session = session;
    }	

    // Recupera o carrinho da sessão ou cria um novo se não existir (PROCESSAMENTO NA SESSÃO)
    public Map<Long, Integer> getCarrinho() {        
    	@SuppressWarnings("unchecked") // Suprime a mensagem de Cast genérico
    	//Tenta recuperar da sessão do carrinho atual "carrinho"); Faz um cast manual para o tipo esperado
        Map<Long, Integer> carrinho = (Map<Long, Integer>) session.getAttribute("carrinho");

        if (carrinho == null) { // Se não houver carrinho salvo na sessão
            carrinho = new HashMap<>(); // Cria um novo mapa
            
            session.setAttribute("carrinho", carrinho); // atualiza na sessão
        }
        return carrinho; //Retorna o carrinho
    }

    // Método para contar o número de produtos no carrinho (PROCESSAMENTO NA SESSÃO)
    public int getQuantidadeTotalItens() {
    	// Obtém o carrinho (produtoId, qt) da sessão
    	Map<Long, Integer> carrinho = getCarrinho();
        return carrinho.size(); //Retorna a quantidade de itens do carrinho
    }

    // Adiciona um produto ao carrinho ou incrementa a quantidade se já estiver presente (PROCESSAMENTO NA SESSÃO)
    public void adicionarProduto(Long produtoId) {
    	// Obtém o carrinho (produtoId, qt) da sessão
    	Map<Long, Integer> carrinho = getCarrinho();

        if (carrinho.containsKey(produtoId)) { //Se o produto existir no carrinho      	       	
        	// Adiciona + 1; Atualiza o carrinho com a nova quantidade        	
        	carrinho.put(produtoId, carrinho.get(produtoId) + 1);
        } else { // se ainda não existir
            carrinho.put(produtoId, 1); // Adiciona 1 na quantidade do produto
        }

        session.setAttribute("carrinho", carrinho); // atualiza na sessão
    }

    // Botão(+) que incrementa a quantidade de um produto no carrinho (PROCESSAMENTO NA SESSÃO)
    public void incrementarQuantidade(Long produtoId) {
    	// Obtém o carrinho (produtoId, qt) da sessão
    	Map<Long, Integer> carrinho = getCarrinho();
    	    	
    	//Se não houver produto, define como "0"; Adiciona + 1; Atualiza o carrinho com a nova quantidade    	
        carrinho.put(produtoId, carrinho.getOrDefault(produtoId, 0) + 1);
        
        session.setAttribute("carrinho", carrinho); // atualiza na sessão
    }

    // Botão(-) que decrementa a quantidade de um produto no carrinho (PROCESSAMENTO NA SESSÃO)
    public void decrementarQuantidade(Long produtoId) {
    	// Obtém o carrinho (produtoId, qt) da sessão
    	Map<Long, Integer> carrinho = getCarrinho();
        int quantidadeAtual = carrinho.getOrDefault(produtoId, 0); // Se não houver qt define como "0"

        if (quantidadeAtual > 1) { // se qt for maior que 1
            carrinho.put(produtoId, quantidadeAtual - 1); // retira um da qt do produto
        } else { // se for igual a um
            carrinho.remove(produtoId); // remove o produto
        }

        session.setAttribute("carrinho", carrinho); // atualiza na sessão
    }

    // Retorna os itens no carrinho, com a quantidade de cada produto
    @Transactional(readOnly = true)
    public List<ItemCarrinhoDTO> getItens() {
        // Obtém o carrinho (produtoId, qt) da sessão
    	Map<Long, Integer> carrinho = getCarrinho(); 
    	
    	//Cria uma lista vazia que será preenchida com os itens do carrinho "ItemCarrinhoDTO"
        List<ItemCarrinhoDTO> itensCarrinho = new ArrayList<>();

        // Itera (pecorre a lista) sobre cada entrada do mapa do carrinho (produto e quantidade).
        for (Map.Entry<Long, Integer> entry : carrinho.entrySet()) {
            ProdutoEntity produto = produtoRepository.findById(entry.getKey()) //Busca no DB o Produto por "ID"
                    .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto com ID " + entry.getKey() +
                    		" não encontrado")); // lança Exception caso não encontre
            
            int quantidade = entry.getValue(); // Recupera a quantidade do produto no carrinho
            
            // Cria um novo ItemCarrinhoDTO com o produto e sua quantidade
            itensCarrinho.add(new ItemCarrinhoDTO(produto, quantidade));
        }

        return itensCarrinho; //Retorna a lista preenchida.
    }


    // Limpa o carrinho da sessão (PROCESSAMENTO NA SESSÃO)
    public void limparCarrinho() {
        session.removeAttribute("carrinho");
    }

    // Método para remover um produto do carrinho  (PROCESSAMENTO NA SESSÃO)
    public void removerProduto(Long produtoId) {
    	// Obtém o carrinho (produtoId, qt) da sessão
        Map<Long, Integer> carrinho = getCarrinho();
        carrinho.remove(produtoId);

        if (carrinho.isEmpty()) { // se carro estiver vazio
            zerarFrete();  // Zera o valor do frete
        }

        session.setAttribute("carrinho", carrinho); // atualiza na sessão
    }

    // Zera o frete quando remove o último item do carrinho (PROCESSAMENTO NA SESSÃO)
    private void zerarFrete() {
        session.setAttribute("frete", BigDecimal.ZERO); // Define como BigDecimal com valor "0"
    }

    // Limpa o carrinho ao finalizar o pedido (PROCESSAMENTO NA SESSÃO)
    public void limparSessaoCompra() {
        session.removeAttribute("carrinho"); // limpa o carrinho
        session.removeAttribute("frete"); // limpa o frete
    }

    // Calcula o valor total do carrinho
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalCarrinho() {
        // obtém a lista de itens do carrinho. Cada item é um "objeto ItemCarrinhoDTO" 
    	List<ItemCarrinhoDTO> itensCarrinho = getItens();
        return itensCarrinho.stream() // inicia o processamento
        		
                .map(item -> item.getProduto().getPreco() // Busca o produto e o valor unitário
                	// Multiplica o valor acima pela quantidade e gera um subtotal de cada produto
                	.multiply(BigDecimal.valueOf(item.getQuantidade()))) 
                	.reduce(BigDecimal.ZERO, BigDecimal::add); // Soma os subtotais acima
    }
}


