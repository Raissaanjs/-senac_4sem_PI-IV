// Modal listproduct.html
// Função para abrir o modal de visualização de produto
document.addEventListener('DOMContentLoaded', function() {
    const buttons = document.querySelectorAll('.btn-outline-dark');

    // Detectar se está no endpoint /produtos/listar
    const isListProductPage = window.location.pathname.includes('/produtos/listar');

    // Para os botões da listagem (fora do modal)
    buttons.forEach(button => {
        const quantidade = parseInt(button.getAttribute('data-quantidade'), 10);
        if (quantidade === 0) {
            button.disabled = true;
            button.textContent = "Sem Estoque";
            button.classList.remove('btn-outline-dark');
            button.classList.add('btn-danger');
        }
    });

    window.openViewProdModal = function(prodId) {
        console.log("Abrindo modal para o ID do produto:", prodId);

        document.getElementById('prodId').value = prodId;
        document.getElementById('modalProdutoId').value = prodId;

        fetch(`/produtos/detalhes/${prodId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Erro ${response.status}: Não foi possível carregar os dados do produto.`);
                }
                return response.json();
            })
            .then(data => {
                console.log("Dados do produto carregados:", data);

                document.getElementById('prodNome').innerText = data.nome || '';
                document.getElementById('prodPreco').innerText = data.preco.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }) || '';
                document.getElementById('prodDescricao').innerText = data.descricao || '';

                const avaliacaoContainer = document.getElementById('prodAvaliacao');
                avaliacaoContainer.innerHTML = '';
                const avaliacao = data.avaliacao || 0;
                for (let i = 1; i <= 5; i++) {
                    const star = document.createElement('span');
                    star.innerHTML = '★';
                    star.className = i <= avaliacao ? 'star' : '';
                    avaliacaoContainer.appendChild(star);
                }

                const modalComprarBtn = document.querySelector('#viewProdModal button[type="submit"]');

                // Aqui a lógica que ajusta o botão dentro do modal para o endpoint /produtos/listar
                if (isListProductPage) {
                    if (data.quantidade === 0) {
                        modalComprarBtn.disabled = true;
                        modalComprarBtn.textContent = "Sem Estoque";
						modalComprarBtn.classList.remove('btn-success');
						modalComprarBtn.classList.add('btn-danger');
                    } else {
                        modalComprarBtn.disabled = true;
                        modalComprarBtn.textContent = "Comprar";
						modalComprarBtn.classList.remove('btn-danger');
						modalComprarBtn.classList.add('btn-success');
                    }
                } else {
                    // comportamento original para outras páginas (index.html)
                    if (data.quantidade === 0) {
                        modalComprarBtn.disabled = true;
                        modalComprarBtn.textContent = "Sem Estoque";
                        modalComprarBtn.classList.remove('btn-success');
                        modalComprarBtn.classList.add('btn-danger');
                    } else {
                        modalComprarBtn.disabled = false;
                        modalComprarBtn.textContent = "Comprar";
                        modalComprarBtn.classList.remove('btn-danger');
                        modalComprarBtn.classList.add('btn-success');
                    }
                }
            })
            .catch(error => {
                console.error("Erro ao buscar os dados do produto:", error);
                alert("Não foi possível carregar os dados do produto. Tente novamente.");
            });

        fetch(`/produto-imagens/detalhes/${prodId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Erro ${response.status}: Não foi possível carregar as imagens.`);
                }
                return response.json();
            })
            .then(data => {
                console.log("Imagens do produto carregadas:", data);

                const carouselImages = document.getElementById('carouselImages');
                carouselImages.innerHTML = '';

                data.forEach((imagem, index) => {
                    const div = document.createElement('div');
                    div.className = 'carousel-item' + (index === 0 ? ' active' : '');
                    const img = document.createElement('img');
                    img.src = '/uploads/' + imagem.nome;
                    img.className = 'd-block w-100';
                    img.alt = 'Imagem do produto';
                    div.appendChild(img);
                    carouselImages.appendChild(div);
                });
            })
            .catch(error => {
                console.error("Erro ao buscar as imagens do produto:", error);
                alert("Não foi possível carregar as imagens do produto. Tente novamente.");
            });

        const modal = new bootstrap.Modal(document.getElementById('viewProdModal'));
        modal.show();
    };
});

