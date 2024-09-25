package com.tecnologiaefinancas.newcadastroprofissionais.model;

public enum StatusProfessional {
    SELECIONAR("Selecionar"),
    PROFISSIONAL_SUGERIDO("Profissional sugerido"),
    MARCADO("Agendamento Marcado"),
    EFETUADO("Serviço Efetuado");

    private String descricao;

    StatusProfessional(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
