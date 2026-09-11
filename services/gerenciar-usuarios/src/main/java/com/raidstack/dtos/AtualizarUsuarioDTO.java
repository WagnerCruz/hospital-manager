package com.raidstack.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AtualizarUsuarioDTO(

        @NotNull(message = "{usuario.id.obrigatorio}")
        UUID id,

        @NotNull(message = "{usuario.nome.obrigatorio}")
        String nome,

        @NotNull(message = "{usuario.email.obrigatorio}")
        @Email(message = "{usuario.email.invalido}")
        String email,

        @NotNull(message = "{usuario.login.obrigatorio}")
        String login
) {
}
