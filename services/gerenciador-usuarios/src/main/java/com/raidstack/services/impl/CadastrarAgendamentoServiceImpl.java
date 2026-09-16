package com.raidstack.services.impl;


import com.raidstack.dtos.CadastrarAgendamentoDTO;
import com.raidstack.dtos.VisualizarAgendamentoDTO;
import com.raidstack.entities.Agendamento;

import com.raidstack.kafka.events.AgendamentoEvent;
import com.raidstack.kafka.producers.KafkaAgendamentoProducer;
import com.raidstack.mappers.AgendamentoMapper;
import com.raidstack.repositories.IAgendamentoRepository;
import com.raidstack.services.ICadastrarAgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CadastrarAgendamentoServiceImpl implements ICadastrarAgendamentoService {

    @Autowired
    private KafkaAgendamentoProducer kafkaAgendamentoProducer;

    @Autowired
    private IAgendamentoRepository agendamentoRepository;

    public VisualizarAgendamentoDTO cadastrarAgendamentoDTO(CadastrarAgendamentoDTO cadastrarAgenamentoDTO) {
        Agendamento agendamentoNovo = AgendamentoMapper.INSTANCE.cadastrarAgendamentoDTOToAgendamento(cadastrarAgenamentoDTO);

        Agendamento agendamentoSalvo = agendamentoRepository.save(agendamentoNovo);

        this.enviarNotificacaoAgendamentoCriado(agendamentoSalvo);

        return AgendamentoMapper.INSTANCE.agendamentoToVisualizarAgendamentoDTO(agendamentoSalvo);
    }

    private void enviarNotificacaoAgendamentoCriado(Agendamento agendamentoSalvo) {
        AgendamentoEvent agendamentoEvent = AgendamentoMapper.INSTANCE.agentamentoToAgendamentoEvent(agendamentoSalvo);
        this.kafkaAgendamentoProducer.enviarAgendamentoCriado(agendamentoEvent);
    }
}
