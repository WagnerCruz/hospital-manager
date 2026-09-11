package com.raidstack.dtos;

import com.raidstack.enums.PerfilEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record AtualizarUsuarioPerfilDTO(

        @NotNull(message = "{usuario.id.obrigatorio}")
        UUID id,

        @NotEmpty(message = "{usuario.perfil.vazio}")
        @NotNull(message = "{usuario.perfil.obrigatorio}")
        List<PerfilEnum> perfis

) {
}
