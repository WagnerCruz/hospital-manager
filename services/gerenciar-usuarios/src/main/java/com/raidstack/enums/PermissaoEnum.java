package com.raidstack.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermissaoEnum {
    
	USUARIO_ATUALIZAR("Atualizar usuário"),
	USUARIO_ATUALIZAR_SENHA("Atualizar senha"),
	USUARIO_VISUALIZAR("Visualizar usuário"),
	ADMINISTRADOR_USUARIO_CRIAR("Criar usuário"),
	ADMINISTRADOR_USUARIO_ATUALIZAR("Atualizar usuários"),
    ADMINISTRADOR_USUARIO_VISUALIZAR("Visualizar usuários"),
	ADMINISTRADOR_USUARIO_DESATIVAR("Desativar usuários"),
	ADMINISTRADOR_USUARIO_GERENCIAR_PERFIL("Gerenciar perfil dos usuários"),
    ;

    private String descricao;

}
