package com.raidstack.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermissaoEnum {
    
    USUARIO_CRIAR("Criar usuário"),
	USUARIO_ATUALIZAR("Atualizar usuário"),
	USUARIO_ATUALIZAR_SENHA("Atualizar senha"),
	USUARIO_VISUALIZAR("Visualizar usuário"),
	USUARIO_ATUALIZAR_EXTERNO("Atualizar outros usuários"),
    USUARIO_VISUALIZAR_EXTERNO("Visualizar outros usuários"),
	USUARIO_DESATIVAR("Desativar usuário"),
	USUARIO_GERENCIAR_PERFIL("Gerenciar perfil dos usuários"),
    ;

    private String descricao;

}
