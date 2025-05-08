//Altera status do Produto
async function toggleStatusProd(checkbox, prodId) {
    try {
        const prodConfirmation = confirm("Tem certeza de que deseja alterar o Status?");
        if (!prodConfirmation) {
            checkbox.checked = !checkbox.checked;
            return;
        }

        const response = await fetch(`/produtos/${prodId}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) throw new Error('Erro ao atualizar o status');

        const data = await response.json();
        console.log("Estado atualizado no servidor:", data);

        checkbox.checked = data.status;

        const statusCell = document.querySelector(`#prod-status-cell-${prodId}`);
        if (statusCell) {
            statusCell.innerText = data.status ? "Ativo" : "Inativo";
        }

        location.reload();
    } catch (error) {
        console.error('Erro ao atualizar status:', error);
        checkbox.checked = !checkbox.checked;
        alert("Ocorreu um erro ao tentar atualizar o status. Por favor, tente novamente.");
    }
}