package com.raidstack.dtos;

import com.raidstack.entities.Usuario;
import java.time.LocalDateTime;
import java.util.UUID;

public record VisualizarAgendamentoDTO(

    UUID id,

    String descricao,

    String status,

    LocalDateTime dataAgendamento,

    Usuario medico,

    Usuario paciente


) {
}
