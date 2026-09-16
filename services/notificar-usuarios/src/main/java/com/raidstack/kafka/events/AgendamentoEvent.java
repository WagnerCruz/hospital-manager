package com.raidstack.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoEvent {

    private UUID idAgendamento;

    private String nomePaciente;

    private String nomeMedico;

    private LocalDateTime dataAgendamento;

    private String descricao;

    public String getDataAgendamentoMostrar() {
        return this.getDataAgendamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getHorarioAgendamentoMostrar() {
        return this.getDataAgendamento().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

}
