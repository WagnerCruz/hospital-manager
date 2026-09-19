package com.raidstack.services.impl;

import com.raidstack.dtos.VisualizarAgendamentoDTO;
import com.raidstack.entities.Agendamento;
import com.raidstack.entities.Usuario;
import com.raidstack.enums.PerfilEnum;
import com.raidstack.mappers.AgendamentoMapper;
import com.raidstack.repositories.IAgendamentoRepository;
import com.raidstack.repositories.IUsuarioRepository;
import com.raidstack.security.ValidarAcessoProprioUsuarioSecurity;
import com.raidstack.services.IBuscarAgendamentoService;
import com.raidstack.services.IValidarUsuarioService;
import com.raidstack.services.exceptions.ResourceConflictException;
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

    @Autowired
    private ValidarAcessoProprioUsuarioSecurity validarAcessoProprioUsuarioSecurity;

    public Agendamento buscarAgendamento(UUID id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado, ID [" + id + "]"));

        if (this.validarAcessoAgendamento(agendamento)) {
            throw new RuntimeException("");
        }

        return agendamento;
    }

    public VisualizarAgendamentoDTO buscarAgendamentoDTO(UUID id) {
        Agendamento agendamento = buscarAgendamento(id);
        return AgendamentoMapper.INSTANCE.agendamentoToVisualizarAgendamentoDTO(agendamento);
    }

    @Override
    public Page<VisualizarAgendamentoDTO> buscarAgendamentos(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Usuario usuarioLogado = this.validarAcessoProprioUsuarioSecurity.obterUsuarioLogado();

        Page<Agendamento> agendamentosPage = null;

        if (this.validarUsuarioService.validarPerfilUsuarioPorID(usuarioLogado.getId(), PerfilEnum.ENFERMEIRO)) {
            agendamentosPage = this.agendamentoRepository.findAll(pageable);
        } else {
            agendamentosPage = this.agendamentoRepository.findById(usuarioLogado.getId(), pageable);
        }

        return AgendamentoMapper.INSTANCE.agendamentoToVisualizarAgendamentoDTO(agendamentosPage);
    }

    private boolean validarAcessoAgendamento(Agendamento agendamento) {
        Usuario usuarioLogado = this.validarAcessoProprioUsuarioSecurity.obterUsuarioLogado();
        if (this.validarUsuarioService.validarPerfilUsuarioPorID(usuarioLogado.getId(), PerfilEnum.ENFERMEIRO)) {
            return true;
        }
        return agendamento.getMedico().getId().equals(usuarioLogado.getId())
                || agendamento.getPaciente().getId().equals(usuarioLogado.getId());
    }

}
