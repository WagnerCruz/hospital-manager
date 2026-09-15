package com.raidstack.services.impl;


import com.raidstack.dtos.CadastrarAgendamentoDTO;
import com.raidstack.dtos.VisualizarAgendamentoDTO;
import com.raidstack.entities.Agendamento;

import com.raidstack.mappers.AgendamentoMapper;
import com.raidstack.repositories.IAgendamentoRepository;
import com.raidstack.services.ICadastrarAgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CadastrarAgendamentoServiceImpl implements ICadastrarAgendamentoService {


    @Autowired
    private IAgendamentoRepository agendamentoRepository;

    public VisualizarAgendamentoDTO cadastrarAgendamentoDTO(CadastrarAgendamentoDTO cadastrarAgenamentoDTO) {
        Agendamento agendamentoNovo = AgendamentoMapper.INSTANCE.cadastrarAgendamentoDTOToAgendamento(cadastrarAgenamentoDTO);

        Agendamento agendamentoSalvo = agendamentoRepository.save(agendamentoNovo);

        return AgendamentoMapper.INSTANCE.agendamentoToVisualizarAgendamentoDTO(agendamentoSalvo);
    }
}
