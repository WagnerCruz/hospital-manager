package com.raidstack.services.impl;

import com.raidstack.dtos.VisualizarAgendamentoDTO;
import com.raidstack.entities.Agendamento;
import com.raidstack.mappers.AgendamentoMapper;
import com.raidstack.repositories.IAgendamentoRepository;
import com.raidstack.repositories.IUsuarioRepository;
import com.raidstack.security.ValidarAcessoProprioUsuarioSecurity;
import com.raidstack.services.IBuscarAgendamentoService;
import com.raidstack.services.IValidarUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BuscarAgendamentoServiceImpl implements IBuscarAgendamentoService {

    @Autowired
    private IAgendamentoRepository agendamentoRepository;

    @Autowired
    private IValidarUsuarioService validarUsuarioService;

    public Agendamento buscarAgendamento(UUID id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado, ID [" + id + "]"));

        String loginUsuarioLogado = this.obterLoginUsuarioLogado();



        return agendamento;
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

    private String obterLoginUsuarioLogado() {
        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();
        return authentication.getName();
    }

}
