package com.devsoft.rgdi_store.entities;

public enum PedidoStatus {
    PENDENTE_PAGAMENTO("Pendente de Pagamento"),
    PAGO("Pagamento Aprovado"),
    EM_SEPARACAO("Em Separação"),
    ENVIADO("Enviado"),
    ENTREGUE("Entregue"),
    CANCELADO("Cancelado"),
    DEVOLVIDO("Devolvido"),
    REEMBOLSADO("Reembolsado");

    private final String descricao;

    PedidoStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
