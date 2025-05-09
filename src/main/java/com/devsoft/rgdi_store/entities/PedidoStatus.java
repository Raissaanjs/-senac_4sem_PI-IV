package com.devsoft.rgdi_store.entities;

public enum PedidoStatus {
    AGUARDANDO_PAGAMENTO("Aguardando pagamento"),
    PAGAMENTO_REJEITADO("Pagamento rejeitado"),
    PAGAMENTO_COM_SUCESSO("Pagamento com sucesso"),
    AGUARDANDO_RETIRADA("Aguardando retirada"),
    EM_TRANSITO("Em trânsito"),
    ENTREGUE("Entregue");

    private final String descricao;

    PedidoStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
