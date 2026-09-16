package com.raidstack.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CadastrarAgendamentoDTO(

        @NotNull(message = "{agendamento.descricao.obrigatorio}")
        String descricao,

        @NotNull(message = "{agendamento.status.obrigatorio}")
        String status,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        @NotNull(message = "{agendamento.data.obrigatorio}")
        LocalDateTime dataAgendamento,

        @NotNull(message = "{agendamento.medico.obrigatorio}")
        UUID idMedico,

        @NotNull(message = "{agendamento.paciente.obrigatorio}")
        UUID idPaciente

) {
}
