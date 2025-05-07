package com.devsoft.rgdi_store.entities;

public enum PedidoStatus {
    PENDENTE_PAGAMENTO("Aguardando pagamento"),
    PAGTO_REJEITADO("Pagamento rejeitado"),
    PAGTO_SUCESSO("Pagamento com sucesso"),
    AGUARDANDO_RETIRA("Aguardando retirada"),
    ENVIADO("Em trânsito"),
    ENTREGUE("Entregue");

    private final String descricao;

    PedidoStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
