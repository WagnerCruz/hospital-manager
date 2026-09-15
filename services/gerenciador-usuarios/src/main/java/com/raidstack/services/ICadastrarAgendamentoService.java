package com.raidstack.services;


import com.raidstack.dtos.CadastrarAgendamentoDTO;
import com.raidstack.dtos.VisualizarAgendamentoDTO;


public interface ICadastrarAgendamentoService {

    VisualizarAgendamentoDTO cadastrarAgendamentoDTO(CadastrarAgendamentoDTO agendamento);

}
