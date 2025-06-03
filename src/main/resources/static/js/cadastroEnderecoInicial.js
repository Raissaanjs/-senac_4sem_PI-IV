document.addEventListener('DOMContentLoaded', function () {
		const formFaturamento = document.getElementById('formFaturamento');
		const clienteId = /*[[${clienteId}]]*/ null; // Recupera o clienteId do Thymeleaf
		const btnAdicionarEntrega = document.getElementById('btnAdicionarEntrega');
	    const enderecoEntregaSection = document.getElementById('enderecoEntregaSection');
	    
	
	    if (formFaturamento) {
	        formFaturamento.addEventListener('submit', function (event) {
	            event.preventDefault();
	
	            const formData = new FormData(formFaturamento);
	            fetch('/enderecos/noauth/endereco-inicial/faturamento', {
	                method: 'POST',
	                body: formData,
	            })
	                .then(response => response.json())
	                .then(data => {
	                    enderecoEntregaSection.style.display = 'block';
						// Quando clicar no botão Endereço de entrega, envia o form acima e desabilita o botão
	                    btnAdicionarEntrega.disabled = true; 
	                })
	                .catch(error => {
	                    console.error('Erro ao salvar endereço de faturamento:', error);
	                    alert('Erro ao salvar endereço de faturamento.');
	                });
	        });
	    } else {
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