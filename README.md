# -senac_4sem_PI-IV

Sprint 1 - entrega 27/02

Planilha Desenvolvida para Exemplo pelo Professor.

| Sprint | Como Um                 | Eu quero, eu posso         | Para que                                  | Critério de aceite |
|--------|-------------------------|----------------------------|-------------------------------------------|--------------------|
| 1      | Usuário não logado      | Identificar no sistema     | Possa entrar no backoffice               | - Os dados de login devem ser validados no banco de dados.  
|        |                         |                            |                                           | - O login é o e-mail do usuário.  
|        |                         |                            |                                           | - A senha deve ser encriptada na ponta antes de validação com o dado no banco (que também estará encriptado).  
|        |                         |                            |                                           | - Não localizando, o sistema deve negar a entrada do usuário no backoffice.  
|        |                         |                            |                                           | - Logando (usuário e senha corretos e habilitado), o sistema deve cair na página principal do backoffice, onde terá o link para a página de lista de produtos (todos PERSONAS) e lista de usuários para administrador.  
|        |                         |                            |                                           | - Logando, também o sistema deve criar a sessão com o usuário e seu grupo (administrador ou estoquista).  
|        |                         |                            |                                           | - Se um cliente entrar com e-mail e senha, o mesmo deve ser rejeitado. Esta tela de login é apenas para usuários de backoffice.  
| 1      | Administrador           | Listar Usuário             | Possa acessar as opções de incluir, alterar e habilitar/desabilitar usuários. | - Lista todos os usuários cadastrados no sistema na entrada da tela, mostrando o Nome, e-mail, status, Grupo (para administrador).  
|        |                         |                            |                                           | - Ao clicar no sinal de "+", deve enviar para cadastro de usuário (incluir).  
|        |                         |                            |                                           | - Ao clicar em alterar, o sistema deve enviar para Alteração.  
|        |                         |                            |                                           | - Ao clicar em inativar/reativar, o sistema deverá trocar (se ativo passa para inativo ou se inativo passa para ativo).  
|        |                         |                            |                                           | - O listar usuários deve permitir filtrar (por nome de usuário) a lista de usuários do sistema.  
| 1      | Administrador           | Cadastrar um usuário       | Possa incluir um acesso ao backoffice    | - Cadastrar o nome do usuário, CPF, e-mail, senha, grupo (admin/estoquista) no banco de dados.  
|        |                         |                            |                                           | - No cadastro, pedir a senha 2 vezes. Só permitir o cadastro quando as 2 forem iguais.  
|        |                         |                            |                                           | - A senha deve ser encriptada antes de enviar para o banco de dados.  
|        |                         |                            |                                           | - O cadastro de usuário cadastra o grupo como ativo (sempre).  
|        |                         |                            |                                           | - Não é permitido cadastrar dois usuários com o mesmo login (e-mail).  
|        |                         |                            |                                           | - O CPF deve ser validado antes da gravação.  
| 1      | Administrador           | Alterar um usuário         | Possa manter os dados de um usuário no backoffice | - É permitido apenas alterar o grupo (se não for o usuário logado no momento).  
|        |                         |                            |                                           | - É permitido alterar o nome, CPF, senha (sempre validando a senha 2 vezes) e ela deve ser atualizada no banco de forma encriptada.  
|        |                         |                            |                                           | - Não é permitido alterar o e-mail.  
|        |                         |                            |                                           | - Toda alteração deve refletir no banco de dados.  
| 1      | Administrador           | Habilitar e Desabilitar um usuário | Remover ou conceder acesso a um usuário cadastrado no backoffice | - Na mesma tela de listagem de Usuário, o usuário admin poderá alterar o status dos usuários para ativo (se ele estiver inativado) ou inativar (se ele estiver ativo).  
|        |                         |                            |                                           | - Sem a necessidade de entrar em outra página para alteração. A alteração deve apenas ser confirmada por tela de mensagem de alerta pedindo se o usuário concorda ou não com a alteração.  

# Planilha de Desenvolvimento do Trabalho Oficial


| Prioridade | Como um               | Eu quero/Eu posso                         | Para que                                                 |
|------------|----------------------|------------------------------------------|---------------------------------------------------------|
| 1400       | Usuário logado       | Listar usuário, produto ou pedido       | Escolher qual a atividade que vou executar no backoffice |
| 1300       | Usuário administrador | Gestão de usuário                       | Listar, incluir, ativar/desativar usuário               |
| 1200       | Usuário administrador | Gestão de produto                       | Listar, incluir, ativar/desativar e visualizar produto  |
| 1100       | Cliente não logado, cliente logado | Visualizar o detalhe de um produto | Escolher com mais dados se vou ou não comprar o produto |
| 1000       | Cliente não logado, cliente logado | Ver a lista de produtos da empresa | Poder escolher um produto para visualizar/comprar      |
| 900        | Cliente não logado, cliente logado | Colocar um produto no carrinho     | Reservar este produto para compra                      |
| 800        | Cliente não logado, cliente logado | Alterar as quantidades ou remover produtos do carrinho | Gerenciar meu carrinho |
| 700        | Cliente não logado, cliente logado | Simular o frete do meu pedido     | Saber o valor total do pedido                          |
| 600        | Cliente não logado    | Pedir identificação do cliente         | Poder me identificar na loja                           |
| 500        | Cliente não logado    | Pedir para me cadastrar como cliente   | Registrar meus dados e endereços na loja              |
| 450        | Cliente logado        | Selecionar ou incluir um novo endereço | Pedido vá direto para o local correto de entrega      |
| 400        | Cliente logado        | Selecionar a forma de pagamento        | Escolher se vou pagar com boleto ou cartão            |
| 300        | Cliente logado        | Visualizar o resumo do pedido          | Verificar se todas as informações estão corretas      |
| 200        | Cliente logado        | Gerar pedido                           | Gerar o pedido no sistema                             |
| 100        | Cliente logado        | Ver meus pedidos anteriores            | Visualizar pedidos anteriores e seu status            |
| 50         | Cliente logado        | Detalhe do pedido                      | Visualizar todas as informações do pedido já feito   |
| 10         | Usuário não logado    | Identificar no sistema                 | Entrar com usuário e senha e acessar o sistema de backoffice |
| 5          | Usuário estoquista    | Gestão de estoque                      | Alterar o produto                                     |

#separação da planilha para as equipes de Back-end e Front-end , com base nas responsabilidades de cada um.


#Aqui está a separação da planilha para as equipes de **Back-end** e **Front-end**, com base nas responsabilidades de cada um:  

---

### **Back-end**  
(Responsável por lógica de negócio, regras do sistema, banco de dados, autenticação e processamento de pedidos)  

| Prioridade | Quem | Tarefa | Objetivo |
|------------|----------------------|------------------------------------------|---------------------------------------------------------|
| 1400       | Back-end | Listar usuário, produto ou pedido | Criar endpoints para buscar essas informações |
| 1300       | Back-end | Gestão de usuário | Criar endpoints para listar, incluir, ativar/desativar usuário |
| 1200       | Back-end | Gestão de produto | Criar endpoints para listar, incluir, ativar/desativar e visualizar produto |
| 900        | Back-end | Colocar um produto no carrinho | Criar lógica para adicionar um item ao carrinho |
| 800        | Back-end | Alterar quantidades/remover produtos do carrinho | Criar lógica para gerenciar os itens do carrinho |
| 700        | Back-end | Simular o frete | Implementar cálculo de frete via API ou regras definidas |
| 600        | Back-end | Pedir identificação do cliente | Criar endpoint para autenticação/login |
| 500        | Back-end | Cadastro de cliente | Criar endpoint para registro de clientes e endereços |
| 450        | Back-end | Selecionar/incluir endereço | Criar lógica para salvar ou buscar endereços cadastrados |
| 400        | Back-end | Selecionar forma de pagamento | Integrar com APIs de pagamento para processar transações |
| 200        | Back-end | Gerar pedido | Criar lógica para registrar pedidos no sistema |
| 100        | Back-end | Listar pedidos anteriores | Criar endpoint para buscar histórico de pedidos |
| 5          | Back-end | Gestão de estoque | Criar lógica para alterar o estoque dos produtos |

---

### **Front-end**  
(Responsável por interface do usuário, interação com APIs, experiência do usuário e navegação)  

| Prioridade | Quem | Tarefa | Objetivo |
|------------|----------------------|------------------------------------------|---------------------------------------------------------|
| 1100       | Front-end | Visualizar detalhe de um produto | Criar página para exibição de detalhes de produtos |
| 1000       | Front-end | Ver lista de produtos | Criar tela com listagem de produtos da empresa |
| 900        | Front-end | Adicionar produto ao carrinho | Criar botão e interação visual para adicionar itens |
| 800        | Front-end | Alterar/remover produtos do carrinho | Criar interface para editar o carrinho |
| 700        | Front-end | Simular o frete | Criar campo para inserção do CEP e exibição do frete |
| 600        | Front-end | Identificação/login | Criar tela de login e interação com API |
| 500        | Front-end | Cadastro de cliente | Criar formulário de cadastro e interação com API |
| 450        | Front-end | Selecionar/incluir endereço | Criar interface para gerenciamento de endereços |
| 400        | Front-end | Selecionar forma de pagamento | Criar interface para escolha entre boleto/cartão |
| 300        | Front-end | Visualizar resumo do pedido | Criar tela de revisão do pedido antes da compra |
| 200        | Front-end | Gerar pedido | Criar botão para finalizar pedido e enviar para API |
| 100        | Front-end | Ver pedidos anteriores | Criar tela para exibição do histórico de compras |
| 50         | Front-end | Ver detalhes do pedido | Criar página com todas as informações do pedido |
| 10         | Front-end | Tela de login do sistema backoffice | Criar interface para entrada de usuário e senha |

---


