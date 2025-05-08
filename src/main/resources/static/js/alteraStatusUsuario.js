// Altera status do Usuario
async function toggleStatusUser(checkbox, userId) {
    try {
        const userConfirmation = confirm("Tem certeza de que deseja alterar o Status?");
        if (!userConfirmation) {
            checkbox.checked = !checkbox.checked;
            return;
        }

        const response = await fetch(`/usuarios/${userId}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) throw new Error('Erro ao atualizar o status');

        const data = await response.json();
        console.log("Estado atualizado no servidor:", data);

        checkbox.checked = data.status;

        const statusCell = document.querySelector(`#user-status-cell-${userId}`);
        if (statusCell) {
            statusCell.innerText = data.status ? "Ativo" : "Inativo";
        }

        // Força o navegador a recarregar a página
        location.reload();
    } catch (error) {
        console.error('Erro ao atualizar status:', error);
        checkbox.checked = !checkbox.checked;
        alert("Ocorreu um erro ao tentar atualizar o status. Por favor, tente novamente.");
    }
}