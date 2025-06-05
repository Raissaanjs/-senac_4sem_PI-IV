document.addEventListener('DOMContentLoaded', function () {
		// Seleciona o formulário HTML com id="formFaturamento" e armazena na constante formFaturamento
		const formFaturamento = document.getElementById('formFaturamento');	
		// pega elemento HTML com o ID 'btnAdicionarEntrega' e armazena na constante btnAdicionarEntrega
		const btnAdicionarEntrega = document.getElementById('btnAdicionarEntrega');
		// pega elemento HTML com o ID 'enderecoEntregaSection' e armazena na constante enderecoEntregaSection
	    const enderecoEntregaSection = document.getElementById('enderecoEntregaSection');
	    
	
	    if (formFaturamento) { // Verifica se o elemento do formulário existe na página
	        // Adiciona um "ouvinte" de evento para interceptar o envio do formulário
			formFaturamento.addEventListener('submit', function (event) {
	            event.preventDefault(); //  Impede o envio padrão, pois vai usar o fetch 
	
	            const formData = new FormData(formFaturamento); //  Cria um objeto 'FormData' com todos os campos do form
	            fetch('/enderecos/noauth/endereco-inicial/faturamento', { // Faz uma requisição para essa URL no backend
	                method: 'POST', // Usa método de envio POST
	                body: formData, // Envia os dados do formulário sem recarregar a página
	            })
	                .then(response => response.json()) // Retorna uma resposta em JSON do backend, como resultado do envio
	                .then(data => { // Retorna o Objeto JSON 'data', do backend  
	                    enderecoEntregaSection.style.display = 'block'; // Mostra o bloco do endereco de entrega
	                    btnAdicionarEntrega.disabled = true; // Desabilita o botão 'Endereço de entrega'
	                })
	                .catch(error => { // trata os erros do envio do form
	                    console.error('Erro ao salvar endereço de faturamento:', error);
	                    alert('Erro ao salvar endereço de faturamento.');
	                });
	        });
	    } else { // caso não encontre o formulário HTML com id="formFaturamento"
	        console.warn('formFaturamento não encontrado no DOM.');
	    }
	});
	
	//Preencher o Endereço de Entrega como o mesmo do faturamento
	function copiarEnderecoFaturamento() {
	    const usarMesmoEndereco = document.getElementById("copiarEndereco").checked;
	    const campos = ["cep", "logradouro", "numero", "complemento", "bairro", "cidade", "uf"];
	    campos.forEach(campo => {
	        const origem = document.getElementById(`${campo}Faturamento`);
	        const destino = document.getElementById(`${campo}Entrega`);
	        if (origem && destino) {
	            destino.value = usarMesmoEndereco ? origem.value : '';
	        }
	    });
	}