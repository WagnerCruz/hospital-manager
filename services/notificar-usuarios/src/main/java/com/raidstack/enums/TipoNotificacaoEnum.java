package com.raidstack.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoNotificacaoEnum {

    USUARIO_CADASTRAR("Cadastrar", "Cadastrado(a)"),
    USUARIO_ATUALIZAR("Atualizar", "Atualizado(a)"),
    AGENDAMENTO_CADASTRAR("Cadastrar", "Cadastrado"),
    AGENDAMENTO_ATUALIZAR("Atualizar", "Atualizado"),
    ;

    private String operacao;
    private String apresentacao;

}
