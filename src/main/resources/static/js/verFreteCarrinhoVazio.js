// Verifica carrinho vazio e frete não selecionado
document.addEventListener('DOMContentLoaded', function () {
    const finalizarBtn = document.getElementById('finalizarCompra');

    finalizarBtn.addEventListener('click', function (event) {
        event.preventDefault();

        fetch('/carrinho/verificar')
            .then(response => response.json())
            .then(data => {
                if (!data.temItens) {
                    alert('Seu carrinho está vazio. Adicione itens antes de finalizar a compra.');
                    return;
                }

                // Captura o valor do frete exibido no HTML
                const freteTexto = document.querySelector('#freteValor').textContent;
                const valorFrete = parseFloat(freteTexto.replace('R$', '').replace(',', '.').trim());

                if (isNaN(valorFrete) || valorFrete <= 0) {
                    alert('Por favor, escolha um tipo de frete antes de finalizar a compra.');
                    return;
                }

                window.location.href = '/enderecos/auth/endereco-entrega/listar';
            })
            .catch(error => {
                console.error('Erro ao verificar o carrinho:', error);
                alert('Ocorreu um erro ao verificar o carrinho. Tente novamente mais tarde.');
            });
    });
});