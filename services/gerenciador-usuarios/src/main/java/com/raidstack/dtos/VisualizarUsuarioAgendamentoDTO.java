package com.raidstack.dtos;

import com.raidstack.enums.PerfilEnum;
import com.raidstack.enums.PermissaoEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record VisualizarUsuarioAgendamentoDTO(

    UUID id,

    String email,

    String nome

) {
}
