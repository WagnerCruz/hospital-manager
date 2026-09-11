package com.raidstack.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastrarUsuarioDTO(

        @NotNull(message = "{usuario.nome.obrigatorio}")
        String nome,

        @NotNull(message = "{usuario.email.obrigatorio}")
        @Email(message = "{usuario.email.invalido}")
        String email,

        @NotNull(message = "{usuario.login.obrigatorio}")
        String login,

        @Size(min = 8, message = "{usuario.senha.tamanho}")
        @NotNull(message = "{usuario.senha.obrigatorio}")
        String senha

) {
}
