package com.raidstack.dtos;

import com.raidstack.enums.PerfilEnum;
import com.raidstack.enums.PermissaoEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record VisualizarUsuarioDTO (

    UUID id,

    String login,

    String email,

    String nome,

    boolean ativo,

    LocalDateTime dataCriacao,

    LocalDateTime dataAtualizacao,

    List<PerfilEnum> perfis,

    List<PermissaoEnum> permissoes

) {
}
