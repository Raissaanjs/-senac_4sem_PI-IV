document.addEventListener('DOMContentLoaded', function () {
    // Armazena referência ao formulário e botão de envio
    const form = document.getElementById('editProductForm');
    const submitButton = form?.querySelector('button[type="submit"]');

    // Evento de submit - definido apenas uma vez
    if (form) {
        form.addEventListener('submit', function (event) {
            event.preventDefault();

            // Desativa botão para evitar cliques múltiplos
            submitButton.disabled = true;
            submitButton.textContent = "Salvando...";

            // Limpa mensagens de erro e estados inválidos
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

            // Pega o ID salvo no campo hidden
            const prodEditId = document.getElementById('prodEditId').value;

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
                    // Reabilita botão em caso de erro de validação
                    submitButton.disabled = false;
                    submitButton.textContent = "Salvar";

                    response.json().then(errors => {
                        console.error("Erros retornados pelo backend:", errors);
                        Object.keys(errors).forEach(field => {
                            const errorElement = document.getElementById(`${field}Error`);
                            if (errorElement) {
                                errorElement.textContent = errors[field];
                                errorElement.style.display = 'block';
                                const inputField = document.getElementById(field);
                                if (inputField) inputField.classList.add('is-invalid');
                            }
                        });
                    });
                } else {
                    submitButton.disabled = false;
                    submitButton.textContent = "Salvar";
                    alert("Erro ao salvar os dados. Tente novamente.");
                }
            })
            .catch(error => {
                submitButton.disabled = false;
                submitButton.textContent = "Salvar";
                console.error("Erro de comunicação com o servidor:", error);
                alert("Erro de comunicação com o servidor.");
            });
        });
    }

    // Função para abrir e preencher o modal de edição
    window.openEditProdModal = function (prodEditId) {
        console.log("Abrindo modal para o ID do produto:", prodEditId);

        if (!prodEditId) {
            console.error('prodEditId não foi fornecido!');
            return;
        }

        const prodEditIdInput = document.getElementById('prodEditId');
        if (prodEditIdInput) {
            prodEditIdInput.value = prodEditId;
            console.log('prodEditId salvo no campo hidden');
        } else {
            console.error("Não foi possível encontrar o campo prodEditId.");
            return;
        }

        fetch(`/produtos/detalhes/${prodEditId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Erro ${response.status}: Não foi possível carregar os dados.`);
                }
                return response.json();
            })
            .then(data => {
                console.log("Dados carregados do backend:", data);

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
    };
});
