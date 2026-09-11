package com.raidstack.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AtualizarSenhaUsuarioDTO(

        @NotNull(message = "{usuario.id.obrigatorio}")
        UUID id,

        @NotNull(message = "{usuario.login.obrigatorio}")
        String login,

        @Size(min = 8, message = "{usuario.senha.tamanho}")
        @NotNull(message = "{usuario.senha.obrigatorio}")
        String senhaAtual,

        @Size(min = 8, message = "{usuario.senha.tamanho}")
        @NotNull(message = "{usuario.senha.obrigatorio}")
        String novaSenha,

        @Size(min = 8, message = "{usuario.senha.tamanho}")
        @NotNull(message = "{usuario.senha.obrigatorio}")
        String confirmarSenha
) {
}
