package com.raidstack.dtos;

import com.raidstack.entities.Usuario;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CadastrarAgendamentoDTO(

        @NotNull(message = "{agendamento.descricao.obrigatorio}")
        String descricao,

        @NotNull(message = "{agendamento.status.obrigatorio}")
        String status,

        @NotNull(message = "{agendamento.data.obrigatorio}")
        LocalDateTime dataAgendamento,

        @NotEmpty(message = "{agendamento.medico.vazio}")
        @NotNull(message = "{agendamento.medico.obrigatorio}")
        Usuario medico,

        @NotEmpty(message = "{agendamento.paciente.vazio}")
        @NotNull(message = "{agendamento.paciente.obrigatorio}")
        Usuario paciente

) {
}
