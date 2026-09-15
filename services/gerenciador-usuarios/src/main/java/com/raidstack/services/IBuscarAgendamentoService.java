package com.raidstack.services;

import com.raidstack.dtos.VisualizarAgendamentoDTO;
import com.raidstack.entities.Agendamento;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IBuscarAgendamentoService {

    Agendamento buscarAgendamento(UUID id);

    VisualizarAgendamentoDTO buscarAgendamentoDTO(UUID id);

    Page<VisualizarAgendamentoDTO> buscarAgendamentos(int page, int size);

}
