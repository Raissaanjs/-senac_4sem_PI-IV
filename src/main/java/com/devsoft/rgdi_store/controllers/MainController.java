package com.devsoft.rgdi_store.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	/*
    @Autowired
    private AuthenticationService authenticationService;
    
    @Autowired
    private UserRepository userRepository;
   */
	
    @GetMapping("/")
    public String urlBase() {
        return "index"; // Exibe a página da loja virtual
    }
    
    @GetMapping("/front-adm")
    public String frontAdm() {
        return "frontadm"; // Exibe a página seleção (frontadm.html)
    }
    
    @GetMapping("/admin")
    public String posLogin() {
        return "home-admin"; // Renderiza o template home-admin.html
    }
    
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Exibe a página de login (login.html)
    }
    
    @GetMapping("/carrinho")
    public String carinho() {
        return "carrinho";
    }
    
    @GetMapping("/upload-image")
    public String formUploadTest() { //APROVADO
        return "upload-file";
    }
    
    /*
    @PostMapping("/auth/login")
    public ResponseEntity<?> authenticate(@RequestParam("email") String email, 
                                           @RequestParam("password") String password) {
        boolean isAuthenticated = authenticationService.authenticate(email, password);

        if (isAuthenticated) {
            Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

            if (optionalUser.isPresent()) {
                UserEntity usuario = optionalUser.get();

                if (!usuario.isStatus()) {
                    // Log indicando usuário inativo
                    System.out.println("--> Usuário inativo: " + email + " <--\n");

                    // Configura cabeçalho para redirecionamento do usuário inativo
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(URI.create("/error-user-inat"));

                    return ResponseEntity.status(HttpStatus.FOUND)
                            .headers(headers)
                            .build();
                }               

                // Configura cabeçalho para redirecionamento bem-sucedido
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(URI.create("/frontadm"));
                
                // Log para autenticação bem-sucedida
                System.out.println("--> Autenticação bem-sucedida para o email: " + email + " <--\n");                
                //headers.setLocation(URI.create("/achei"));
                
                return ResponseEntity.status(HttpStatus.FOUND)
                        .headers(headers)
                        .build();
            } else {
                // Log para usuário não encontrado
                System.out.println("--> Usuário não encontrado: " + email + " <--\n");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
            }
        } else {
            // Log para falha na autenticação
            System.out.println("\n--> Falha na autenticação para o email: " + email + " <--\n");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }
   */

    @GetMapping("/h2-console")
    public String redirectToH2Console() {
        return "redirect:/h2-console"; // Redireciona para o console do H2
    }
}

