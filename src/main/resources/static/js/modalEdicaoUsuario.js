// Modal para edição de usuário
window.openEditModal = function (userId) {
    console.log("Abrindo modal para o ID:", userId);

    const emailLogado = document.querySelector('meta[name="user-email-logado"]').getAttribute('content');
    document.getElementById('userId').value = userId;

    fetch(`/usuarios/detalhes/${userId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Erro ${response.status}: Não foi possível carregar os dados.`);
            }
            return response.json();
        })
        .then(data => {
            console.log("Dados carregados do backend:", data);

            // Preenche os campos do modal
            document.getElementById('nome').value = data.nome || '';
            document.getElementById('cpf').value = data.cpf || '';
            document.getElementById('senha').value = '';
            document.getElementById('confirmasenha').value = '';
            document.getElementById('grupo').value = data.grupo || '';

            // Desabilita o campo grupo se for o usuário logado
            const grupoSelect = document.getElementById('grupo');
            if (data.email === emailLogado) {
                grupoSelect.setAttribute('disabled', 'disabled');
            } else {
                grupoSelect.removeAttribute('disabled');
            }

            // Exibe o modal
            const modal = new bootstrap.Modal(document.getElementById('editUserModal'));
            modal.show();

            modal._element.addEventListener('shown.bs.modal', function () {
                const form = document.getElementById('editUserForm');

                if (form) {
                    form.addEventListener('submit', function (event) {
                        event.preventDefault();

                        document.querySelectorAll('.invalid-feedback').forEach(el => el.style.display = 'none');
                        document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));

                        const userId = document.getElementById('userId').value;
                        const userData = {
                            nome: document.getElementById('nome').value.trim(),
                            cpf: document.getElementById('cpf').value.trim(),
                            grupo: document.getElementById('grupo').value,
                            ...(document.getElementById('senha').value && { senha: document.getElementById('senha').value }),
                            ...(document.getElementById('confirmasenha').value && { confirmasenha: document.getElementById('confirmasenha').value })
                        };

                        fetch(`/usuarios/modal-update/${userId}`, {
                            method: 'PUT',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify(userData)
                        })
                        .then(response => {
                            if (response.ok) {
                                alert("Usuário atualizado com sucesso!");
                                location.reload();
                            } else if (response.status === 400) {
                                response.json().then(errors => {
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
                                alert("Erro ao salvar os dados. Tente novamente.");
                            }
                        })
                        .catch(error => {
                            console.error("Erro de comunicação com o servidor:", error);
                            alert("Erro de comunicação com o servidor.");
                        });
                    });
                } else {
                    console.error('Formulário com id "editUserForm" não encontrado!');
                }
            });
        })
        .catch(error => {
            console.error("Erro ao buscar os dados do usuário:", error);
            alert("Não foi possível carregar os dados do usuário. Tente novamente.");
        });
};
