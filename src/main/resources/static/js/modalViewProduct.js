// Modal listproduct.html
// Função para abrir o modal de visualização de produto
document.addEventListener('DOMContentLoaded', function() {
    window.openViewProdModal = function (prodId) {
        console.log("Abrindo modal para o ID do produto:", prodId);

        // Salva o prodId no campo hidden do formulário
        document.getElementById('prodId').value = prodId;
        document.getElementById('modalProdutoId').value = prodId;

        // Faz a requisição para obter os dados do produto (nome, preço, avaliação, etc.)
        fetch(`/produtos/detalhes/${prodId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Erro ${response.status}: Não foi possível carregar os dados do produto.`);
                }
                return response.json();
            })
            .then(data => {
                console.log("Dados do produto carregados:", data);

                // Preenche os campos do modal de produto com os dados recebidos
                document.getElementById('prodNome').innerText = data.nome || '';
                document.getElementById('prodPreco').innerText = data.preco.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }) || '';
                document.getElementById('prodDescricao').innerText = data.descricao || '';

                // Transforma a avaliação em estrelas
                const avaliacaoContainer = document.getElementById('prodAvaliacao');
                avaliacaoContainer.innerHTML = ''; // Limpa o conteúdo anterior
                const avaliacao = data.avaliacao || 0;
                for (let i = 1; i <= 5; i++) {
                    const star = document.createElement('span');
                    star.innerHTML = '★';
                    star.className = i <= avaliacao ? 'star' : '';
                    avaliacaoContainer.appendChild(star);
                }
            })
            .catch(error => {
                console.error("Erro ao buscar os dados do produto:", error);
                alert("Não foi possível carregar os dados do produto. Tente novamente.");
            });

        // Faz a requisição para obter as imagens do produto para o carrossel
        fetch(`/produto-imagens/detalhes/${prodId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Erro ${response.status}: Não foi possível carregar as imagens.`);
                }
                return response.json();
            })
            .then(data => {
                console.log("Imagens do produto carregadas:", data);

                // Limpa as imagens existentes no carrossel
                const carouselImages = document.getElementById('carouselImages');
                carouselImages.innerHTML = ''; // Limpa as imagens antigas

                // Adiciona as novas imagens ao carrossel
                data.forEach((imagem, index) => {
                    const div = document.createElement('div');
                    div.className = 'carousel-item' + (index === 0 ? ' active' : ''); // Marca a primeira imagem como 'active'
                    const img = document.createElement('img');
                    img.src = '/uploads/' + imagem.nome; // Supondo que as imagens estejam na pasta 'uploads'
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

        // Exibe o modal de produto
        const modal = new bootstrap.Modal(document.getElementById('viewProdModal'));
    modal.show();
    };
});