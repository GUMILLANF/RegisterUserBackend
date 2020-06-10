package com.gustavofernandes.registeruserbackend.model.Enum;

public enum Status {

    AGUARDANDO_VALIDACAO("Aguardando validação pelo e-mail."),
    EMAIL_NAO_ENVIADO("Aguardando validação, e-mail não enviado."),
    VALIDADO("Validado.");

    private final String descricao;

    Status(String descricao) { this.descricao = descricao; }

    public String getDescricao() { return descricao; }

}
