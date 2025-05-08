//Modal Edit prod
document.addEventListener('DOMContentLoaded', function () {
window.openEditProdModal = function (prodEditId) {
    console.log("Abrindo modal para o ID do produto:", prodEditId);

    // Verifica se o ID do produto foi passado corretamente
    if (!prodEditId) {
        console.error('prodEditId não foi fornecido!');
        return;
    }

    // Salva o prodEditId no campo hidden do formulário com o id 'prodEditId'
    const prodEditIdInput = document.getElementById('prodEditId');
    if (prodEditIdInput) {
        prodEditIdInput.value = prodEditId; // Atribui o valor ao campo com id 'prodEditId'
        console.log('prodEditId salvo no campo hidden');
    } else {
        console.error("Não foi possível encontrar o campo prodEditId.");
        return;  // Se o campo não existir, pare a execução aqui
    }

    // Faz a requisição para obter os detalhes do produto
    fetch(`/produtos/detalhes/${prodEditId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Erro ${response.status}: Não foi possível carregar os dados.`);
            }
            return response.json();
        })
        .then(data => {
            console.log("Dados carregados do backend:", data);

            // Preenche os campos do modal com os dados recebidos
            document.getElementById('prodEditNome').value = data.nome || '';
            document.getElementById('prodEditPreco').value = data.preco || '';
            document.getElementById('prodEditQuantidade').value = data.quantidade || '';
            document.getElementById('prodEditAvaliacao').value = data.avaliacao || '';
            document.getElementById('prodEditDescricao').value = data.descricao || '';
        })
        .catch(error => {
            console.error("Erro ao buscar os dados do produto:", error);
            alert("Não foi possível carregar os dados do produto. Tente novamente.");
        });

        // Exibe o modal
        const modal = new bootstrap.Modal(document.getElementById('editProductModal'));
        modal.show();

        // Adiciona o evento 'submit' ao formulário após o modal ser exibido
        modal._element.addEventListener('shown.bs.modal', function () {
            const form = document.getElementById('editProductForm');
            if (form) {
                form.addEventListener('submit', function (event) {
                    event.preventDefault(); // Evita o comportamento padrão do formulário

                    // Resetar mensagens de erro e estados inválidos
                    document.querySelectorAll('.invalid-feedback').forEach(el => el.style.display = 'none');
                    document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));

                    // Coleta os dados do formulário
                    const produtoData = {
                        nome: document.getElementById('prodEditNome').value.trim(),
                        preco: parseFloat(document.getElementById('prodEditPreco').value),
                        quantidade: parseInt(document.getElementById('prodEditQuantidade').value),
                        avaliacao: parseInt(document.getElementById('prodEditAvaliacao').value),
                        descricao: document.getElementById('prodEditDescricao').value.trim()
                    };

                    // Envia os dados para o backend usando PUT
                    fetch(`/produtos/update/${prodEditId}`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(produtoData)
                    })
                    .then(response => {
                        if (response.ok) {
                            alert("Produto atualizado com sucesso!");
                            location.reload(); // Atualiza a página
                        } else if (response.status === 400) {
                            // Lida com erros retornados pelo backend
                            response.json().then(errors => {
                                console.error("Erros retornados pelo backend:", errors);
                                Object.keys(errors).forEach(field => {
                                    const errorElement = document.getElementById(`${field}Error`);
                                    if (errorElement) {
                                        errorElement.textContent = errors[field]; // Insere apenas a mensagem do erro
                                        errorElement.style.display = 'block'; // Exibe o erro
                                        const inputField = document.getElementById(field);
                                        if (inputField) inputField.classList.add('is-invalid'); // Marca o campo como inválido
                                    }
                                });
                            });
                        } else {
                            // Mensagem genérica para outros erros
                            alert("Erro ao salvar os dados. Tente novamente.");
                        }
                    })
                    .catch(error => {
                        console.error("Erro de comunicação com o servidor:", error);
                        alert("Erro de comunicação com o servidor.");
                    });
                });
            } else {
                console.error('Formulário com id "editProductForm" não encontrado!');
            }
        });
    };
});