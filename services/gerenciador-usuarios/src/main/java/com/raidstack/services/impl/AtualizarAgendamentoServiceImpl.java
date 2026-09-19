package com.raidstack.services.impl;

import com.raidstack.dtos.*;
import com.raidstack.entities.Agendamento;
import com.raidstack.entities.Perfil;
import com.raidstack.entities.Usuario;
import com.raidstack.kafka.events.AgendamentoEvent;
import com.raidstack.kafka.events.UsuarioEvent;
import com.raidstack.kafka.producers.KafkaAgendamentoProducer;
import com.raidstack.kafka.producers.KafkaUsuarioProducer;
import com.raidstack.mappers.AgendamentoMapper;
import com.raidstack.mappers.UsuarioMapper;
import com.raidstack.repositories.IAgendamentoRepository;
import com.raidstack.repositories.IUsuarioRepository;
import com.raidstack.services.IAtualizarAgendamentoService;
import com.raidstack.services.IAtualizarUsuarioService;
import com.raidstack.services.IBuscarPerfilService;
import com.raidstack.services.IValidarUsuarioService;
import com.raidstack.services.exceptions.ResourceBadRequestException;
import com.raidstack.services.exceptions.ResourceConflictException;
import com.raidstack.utils.GeradorSenhaTemporaria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AtualizarAgendamentoServiceImpl implements IAtualizarAgendamentoService {

    @Autowired
    private IAgendamentoRepository agendamentoRepository;

    @Autowired
    private KafkaAgendamentoProducer kafkaAgendamentoProducer;

    public VisualizarAgendamentoDTO atualizarAgendamento(AtualizarAgendamentoDTO agendamentoDTO) {
        Agendamento agendamento = this.buscarAgendamento(agendamentoDTO.id());

        Agendamento agendamentoAtualizado = AgendamentoMapper.INSTANCE.atualizarAgendamentoDTOToAgendamento(agendamentoDTO);

        List<String> errosValidacao = new ArrayList<>();
        this.verificarErrosValidacao(errosValidacao);

        this.atualizarInformacoesAgendamento(agendamentoAtualizado, agendamento);

        Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);
        this.enviarNotificacaoAtualizacaoAgendamento(agendamentoSalvo);

        return AgendamentoMapper.INSTANCE.agendamentoToVisualizarAgendamentoDTO(agendamentoSalvo);
    }

    private void atualizarInformacoesAgendamento(Agendamento agendamentoAtualizado, Agendamento agendamento) {
        agendamento.setDescricao(agendamentoAtualizado.getDescricao());
        agendamento.setStatus(agendamentoAtualizado.getStatus());
        agendamento.setMedico(agendamentoAtualizado.getMedico());
        agendamento.setPaciente(agendamentoAtualizado.getPaciente());
        agendamento.setDataAgendamento(agendamentoAtualizado.getDataAgendamento());
    }

    private void verificarErrosValidacao(List<String> errosValidacao) {
        if (!errosValidacao.isEmpty()) {
            throw new ResourceConflictException("Erro ao validar Usuário: ", errosValidacao);
        }
    }

    private Agendamento buscarAgendamento(UUID idAgendamento) {
        Optional<Agendamento> usuario = this.agendamentoRepository.findById(idAgendamento);
        return usuario.orElseThrow(() -> new ResourceBadRequestException("Agendamento não encontrado para o ID: " + idAgendamento));
    }


    private void enviarNotificacaoAtualizacaoAgendamento(Agendamento agendamento) {
        AgendamentoEvent agendamentoEvent = AgendamentoMapper.INSTANCE.agendamentoToAgendamentoEvent(agendamento);
        this.kafkaAgendamentoProducer.enviarAgendamentoAtualizado(agendamentoEvent);
    }

}
