package com.raidstack.dtos;

import jakarta.validation.constraints.NotNull;

public record AutenticarUsuarioDTO(
        @NotNull(message = "{usuario.login.obrigatorio}")
        String login,

        @NotNull(message = "{usuario.senha.obrigatorio}")
        String senha
) {
}
