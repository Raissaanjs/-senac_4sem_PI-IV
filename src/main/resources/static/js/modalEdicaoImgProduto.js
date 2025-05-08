// edita imagem do produto
// Modal Edit imagemProd
document.addEventListener('DOMContentLoaded', function () {
window.openEditProdImgModal = function (editImgProdId) {
    console.log("Abrindo modal para o ID do produto:", editImgProdId);

    // Definir o ID do produto no campo oculto do formulário
    document.getElementById('editImgProdId').value = editImgProdId;

    // Exibir o modal
    $('#OpenEditImgProdModal').modal('show');

    // Adiciona o event listener ao formulário quando o modal for exibido
    $('#OpenEditImgProdModal').on('shown.bs.modal', function () {
        var editImagProdForm = document.getElementById('editImagProdForm');
        if (editImagProdForm) {
            editImagProdForm.addEventListener('submit', function(event) {
                event.preventDefault(); // Impede o envio padrão do formulário

                var formData = new FormData(editImagProdForm);

                var id = document.getElementById('editImgProdId').value;
                fetch(`/produto-imagens/update/${id}`, {
                    method: 'PUT',
                    body: formData
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Erro ao atualizar a imagem');
                    }
                    return response.json();
                })
                .then(data => {
                    // Lógica para tratar a resposta de sucesso
                    console.log('Imagem atualizada com sucesso:', data);
                    // Fechar o modal e atualizar a interface conforme necessário
                    $('#OpenEditImgProdModal').modal('hide');
                    // Atualize a interface ou redirecione conforme necessário
                })
                .catch(error => {
                    console.error('Erro:', error);
                    // Lógica para tratar erros
                    alert('Erro ao atualizar a imagem');
                });
            });
        } else {
            console.error('Elemento editImagProdForm não encontrado no DOM.');
        }
    });
    };
});