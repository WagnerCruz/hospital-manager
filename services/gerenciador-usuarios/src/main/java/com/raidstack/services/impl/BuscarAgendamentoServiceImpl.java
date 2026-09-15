package com.raidstack.services.impl;

import com.raidstack.dtos.VisualizarAgendamentoDTO;
import com.raidstack.dtos.VisualizarUsuarioDTO;
import com.raidstack.entities.Agendamento;
import com.raidstack.entities.Usuario;
import com.raidstack.mappers.AgendamentoMapper;
import com.raidstack.mappers.UsuarioMapper;
import com.raidstack.repositories.IAgendamentoRepository;
import com.raidstack.repositories.IUsuarioRepository;
import com.raidstack.services.IBuscarAgendamentoService;
import com.raidstack.services.IBuscarUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BuscarAgendamentoServiceImpl implements IBuscarAgendamentoService {

    @Autowired
    private IAgendamentoRepository agendamentoRepository;

    public Agendamento buscarAgendamento(UUID id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado, ID [" + id + "]"));
    }

    public VisualizarAgendamentoDTO buscarAgendamentoDTO(UUID id) {
        Agendamento agendamento = buscarAgendamento(id);
        return AgendamentoMapper.INSTANCE.agendamentoToVisualizarAgendamentoDTO(agendamento);
    }

    @Override
    public Page<VisualizarAgendamentoDTO> buscarAgendamentos(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Agendamento> agendamentosPage = this.agendamentoRepository.findAll(pageable);
        return AgendamentoMapper.INSTANCE.agendamentoToVisualizarAgendamentoDTO(agendamentosPage);
    }

}
