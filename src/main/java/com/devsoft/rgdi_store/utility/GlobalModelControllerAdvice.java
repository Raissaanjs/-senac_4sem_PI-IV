package com.devsoft.rgdi_store.utility;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.devsoft.rgdi_store.authentication.CustomClienteDetails;
import com.devsoft.rgdi_store.authentication.CustomUserDetails;
import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.entities.UserGroup;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.repositories.UserRepository;
import com.devsoft.rgdi_store.services.CarrinhoService;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalModelControllerAdvice {

    private final UserRepository userRepository;
    private final ClienteRepository clienteRepository;
    private final CarrinhoService carrinhoService;

    public GlobalModelControllerAdvice(UserRepository userRepository, ClienteRepository clienteRepository, CarrinhoService carrinhoService) {
        this.userRepository = userRepository;
        this.clienteRepository = clienteRepository;
        this.carrinhoService = carrinhoService;
    }

    // Adiciona atributos comuns ao objeto `Model`, que estarão disponíveis em todas as views.
    @ModelAttribute
    public void addAttributesToModel(Authentication authentication, Model model) {
        String nomeExibicao = "Visitante"; // Valor padrão se ninguém estiver logado

        // Se o usuário está autenticado
        if (authentication != null) {
        	model.addAttribute("logado", "Sim"); // Marca que há um usuário logado
        	
            Object principal = authentication.getPrincipal(); // Objeto que representa o usuário logado
            
            // Se o usuário for um UserEntity (ex: admin, estoquista)
            if (principal instanceof CustomUserDetails userDetails) {
            	String emailLogado = userDetails.getUsername(); // Pega e-mail do user
            	
                nomeExibicao = userRepository.findByEmail(userDetails.getUsername())
                        .map(UserEntity::getNome)
                        .orElse("Usuário");
                
                model.addAttribute("userEmailLogado", emailLogado); // ← aqui

             // Se o usuário for um ClienteEntity
            } else if (principal instanceof CustomClienteDetails clienteDetails) {
            	String emailLogado = clienteDetails.getUsername(); // Pega e-mail do cliente
            	
            	nomeExibicao = clienteRepository.findByEmail(clienteDetails.getUsername())
                        .map(ClienteEntity::getNome)
                        .orElse("Cliente");
            	
            	model.addAttribute("clienteEmailLogado", emailLogado);
            }

            // Define o nome do usuário para exibição
            model.addAttribute("userName", nomeExibicao);

            // Define as permissões (roles) no modelo para controle de acesso nas views
            model.addAttribute("isAdmin", hasAuthority(authentication, "ROLE_ADMIN"));
            model.addAttribute("isEstoque", hasAuthority(authentication, "ROLE_ESTOQ"));
            model.addAttribute("isUser", hasAuthority(authentication, "ROLE_USER"));
            model.addAttribute("isClient", hasAuthority(authentication, "ROLE_CLIENT"));

            // Tenta obter uma descrição amigável do grupo do usuário
            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER");

            try {
                UserGroup userGroup = UserGroup.fromGrupo(role); // Converte role para enum com descrição
                model.addAttribute("userGroup", userGroup.getDescricao());
            } catch (IllegalArgumentException e) {
                model.addAttribute("userGroup", "Desconhecido");
            }

        } else {
        	// Caso o usuário não esteja autenticado
        	model.addAttribute("logado", "Não"); // filtra usuário não Logado
            model.addAttribute("userName", "Guest");
            model.addAttribute("userGroup", "Visitante");
            model.addAttribute("isAdmin", false);
            model.addAttribute("isEstoque", false);
            model.addAttribute("isUser", false);
            model.addAttribute("isClient", false);
        }

        // Adiciona a quantidade total de itens no carrinho ao modelo
        int totalItens = carrinhoService.getQuantidadeTotalItens();
        model.addAttribute("totalItens", totalItens);
    }

    // Método auxiliar que verifica se o usuário possui determinada autoridade (ROLE)
    private boolean hasAuthority(Authentication auth, String authority) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }
}

