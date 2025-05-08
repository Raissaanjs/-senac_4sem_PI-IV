window.onload = function() {
    // Pegando as variáveis diretamente dos atributos data-* do elemento
    const produtoContainer = document.getElementById('produtoContainer');
    
    // Pegando os valores de paginação dos atributos data-* do container
    let nextPage = parseInt(produtoContainer.getAttribute('data-next-page'));  // Valor da próxima página
    let hasNext = produtoContainer.getAttribute('data-has-next') === 'true';  // Verificando se há mais produtos
    let loading = false;  // Variável para controlar o estado de carregamento
    
    // Lida com o evento de scroll
    window.addEventListener('scroll', () => {
        // Se há próxima página e não está carregando
        if (hasNext && !loading) {
            const scrollTop = document.documentElement.scrollTop;
            const windowHeight = window.innerHeight;
            const scrollHeight = document.documentElement.scrollHeight;

            // Quando o scroll atingir o fim da página (ou quase)
            if (scrollTop + windowHeight >= scrollHeight - 100) {
                loading = true;  // Marca que está carregando novos produtos
                fetch(`/produtos/pagina?page=${nextPage}`)  // Faz a requisição para carregar mais produtos
                    .then(res => res.text())
                    .then(html => {
                        // Insere o HTML retornado no container de produtos
                        produtoContainer.insertAdjacentHTML('beforeend', html);
                        
                        nextPage++;  // Incrementa a página para a próxima requisição
                        hasNext = html.includes('class="col mb-5"');  // Verifica se há mais produtos
                        loading = false;  // Marca que o carregamento terminou

                        // Se não houver mais produtos, esconde o botão "Carregar Mais Produtos"
                        if (!hasNext) {
                            document.getElementById('loadMore').style.display = 'none';
                        }

                        // Atualiza os atributos data-* no produtoContainer para refletir o estado atual
                        produtoContainer.setAttribute('data-next-page', nextPage);
                        produtoContainer.setAttribute('data-has-next', hasNext.toString());
                    })
                    .catch(error => {
                        console.error('Erro ao carregar mais produtos:', error);
                        loading = false;  // Marca que o carregamento falhou
                    });
            }
        }
    });

    // Lidar com o clique no botão "Carregar Mais Produtos"
    const loadMoreButton = document.getElementById('loadMoreButton');
    if (loadMoreButton) {
        loadMoreButton.addEventListener('click', () => {
            if (hasNext && !loading) {
                loading = true;  // Marca que está carregando
				fetch(`/produtos/pagina?page=${nextPage}`)
				    .then(res => res.text())
				    .then(html => {
				        document.getElementById('produtoContainer').insertAdjacentHTML('beforeend', html);
				        nextPage++; // Incrementa a página
				        hasNext = html.includes('class="col mb-5"'); // Verifica se há mais produtos
				        loading = false;

				        // Forçar um reflow para garantir que o layout seja recalculado
				        document.getElementById('produtoContainer').offsetHeight;

				        // Se não houver mais produtos, esconde o botão "Carregar Mais Produtos"
				        if (!hasNext) {
				            document.getElementById('loadMore').style.display = 'none';
				        }
				    })
				    .catch(error => {
				        console.error('Erro ao carregar mais produtos:', error);
				        loading = false; // Marca que o carregamento falhou
				    });
            }
        });
    }
};
