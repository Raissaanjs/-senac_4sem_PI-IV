# -senac_4sem_PI-IV

Sprint 1 - entrega 27/02

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

