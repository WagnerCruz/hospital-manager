package com.raidstack.dtos;

import com.raidstack.enums.PerfilEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record AtualizarUsuarioExternoDTO(

        @NotNull(message = "{usuario.id.obrigatorio}")
        UUID id,

        @NotNull(message = "{usuario.nome.obrigatorio}")
        String nome,

        @NotNull(message = "{usuario.email.obrigatorio}")
        @Email(message = "{usuario.email.invalido}")
        String email,

        @NotNull(message = "{usuario.login.obrigatorio}")
        String login,

        @NotEmpty(message = "{usuario.perfil.vazio}")
        @NotNull(message = "{usuario.perfil.obrigatorio}")
        List<PerfilEnum> perfis

) {
}
