document.addEventListener("DOMContentLoaded", function () {
        document.getElementById("cepFaturamento").addEventListener("blur", function () {
            buscarEndereco("cepFaturamento", "Faturamento");
        });

        document.getElementById("cepEntrega").addEventListener("blur", function () {
            buscarEndereco("cepEntrega", "Entrega");
        });
    });

	//API ViaCEP
    function buscarEndereco(cepInputId, tipoEndereco) {
        const cep = document.getElementById(cepInputId).value.replace(/\D/g, '');

        if (!/^\d{8}$/.test(cep)) {
            console.warn("CEP inválido");
            return;
        }

        const campos = ['logradouro', 'bairro', 'cidade', 'uf'];
        campos.forEach(campo => {
            const input = document.getElementById(`${campo}${tipoEndereco}`);
            if (input) input.value = '';
        });

        fetch(`https://viacep.com.br/ws/${cep}/json/`)
            .then(res => res.json())
            .then(data => {
                if (data.erro) {
                    alert("CEP não encontrado.");
                    return;
                }
                document.getElementById(`logradouro${tipoEndereco}`).value = data.logradouro || '';
                document.getElementById(`bairro${tipoEndereco}`).value = data.bairro || '';
                document.getElementById(`cidade${tipoEndereco}`).value = data.localidade || '';
                document.getElementById(`uf${tipoEndereco}`).value = data.uf || '';
            })
            .catch(() => alert("Erro ao buscar o CEP."));
    }
    
    //API ViaCEP - Endereço único
    function buscarEnderecoUnico(cepInputId) {
	    const cep = document.getElementById(cepInputId).value.replace(/\D/g, '');
	
	    if (!/^\d{8}$/.test(cep)) {
	        console.warn("CEP inválido");
	        return;
	    }
	
	    const campos = ['logradouro', 'bairro', 'cidade', 'uf'];
	    campos.forEach(campo => {
	        const input = document.getElementById(campo);
	        if (input) input.value = '';  // Limpa os campos antes de preencher
	    });
	
	    // Faz a requisição à API ViaCEP
	    fetch(`https://viacep.com.br/ws/${cep}/json/`)
	        .then(res => res.json())
	        .then(data => {
	            if (data.erro) {
	                alert("CEP não encontrado.");
	                return;
	            }
	
	            // Preenche os campos com os dados retornados
	            document.getElementById('logradouro').value = data.logradouro || '';
	            document.getElementById('bairro').value = data.bairro || '';
	            document.getElementById('cidade').value = data.localidade || '';
	            document.getElementById('uf').value = data.uf || '';
	        })
	        .catch(() => alert("Erro ao buscar o CEP."));
	}