document.addEventListener('DOMContentLoaded', function () {
    const radioCartao = document.getElementById('pagamentoCartao');
    const radioPix = document.getElementById('pagamentoPix');
    const radioBoleto = document.getElementById('pagamentoBoleto');
    const cardCredito = document.getElementById('cardCartaoCredito');
    const form = document.getElementById('formPagamento');

    const nomeInput = document.getElementById('nomeCompleto');
    const numeroInput = document.getElementById('numeroCartao');
    const vencimentoInput = document.getElementById('vencimento');
    const codigoInput = document.getElementById('codigo');
    const parcelasInput = document.getElementById('parcelas');
    const erroDiv = document.getElementById('erroVencimento');

    const camposCartao = [nomeInput, numeroInput, vencimentoInput, codigoInput, parcelasInput];

    function toggleCardCredito() {
        const mostrar = radioCartao.checked;
        cardCredito.style.display = mostrar ? 'block' : 'none';

        camposCartao.forEach(campo => {
            if (mostrar) {
                campo.setAttribute('required', 'required');
            } else {
                campo.removeAttribute('required');
            }
        });
    }

    // Aplica máscaras
    $(function () {
        $('#numeroCartao').mask('0000 0000 0000 0000');
        $('#vencimento').mask('00/00');
        $('#codigo').mask('000');
        $('#parcelas').mask('00');
    });

    // Atualiza exibição ao mudar forma de pagamento
    [radioCartao, radioPix, radioBoleto].forEach(radio => {
        radio.addEventListener('change', toggleCardCredito);
    });

    toggleCardCredito(); // Inicializa

    // Transforma nome para uppercase
    nomeInput.addEventListener('input', function () {
        nomeInput.value = nomeInput.value.toUpperCase();
    });

    // Validação ao enviar
    form.addEventListener('submit', function (event) {
        erroDiv.classList.add('d-none');
        erroDiv.innerText = '';

        if (radioCartao.checked) {
            let erro = null;

            const vencimento = vencimentoInput.value;
            const [mesStr, anoStr] = vencimento.split('/');
            const mes = parseInt(mesStr, 10);
            const ano = parseInt(anoStr, 10);

            const hoje = new Date();
            const anoAtual = hoje.getFullYear() % 100;
            const mesAtual = hoje.getMonth() + 1;

            // Vencimento inválido
            if (!mes || !ano || isNaN(mes) || isNaN(ano)) {
                erro = "Data de vencimento inválida.";
            } else if (mes < 1 || mes > 12) {
                erro = "O mês do vencimento deve estar entre 01 e 12.";
            } else if (ano < anoAtual || (ano === anoAtual && mes < mesAtual)) {
                erro = "A data de vencimento não pode estar no passado.";
            }

            // Número do cartão não pode ser tudo zero
            const numeroCartao = numeroInput.value.replace(/\s/g, '');
            if (!erro && /^0+$/.test(numeroCartao)) {
                erro = "O número do cartão não pode ser tudo zero.";
            }

            // Código verificador
            const codigo = codigoInput.value;
            if (!erro && /^0+$/.test(codigo)) {
                erro = "O código verificador não pode ser tudo zero.";
            }

            // Parcelas
            const parcelas = parcelasInput.value;
            if (!erro && /^0+$/.test(parcelas)) {
                erro = "A quantidade de parcelas não pode ser zero.";
            }

            if (erro) {
                event.preventDefault();
                erroDiv.classList.remove('d-none');
                erroDiv.innerText = erro;
                erroDiv.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }
    });
});
