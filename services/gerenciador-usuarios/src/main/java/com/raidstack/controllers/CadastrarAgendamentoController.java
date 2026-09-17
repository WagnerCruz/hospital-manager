package com.raidstack.controllers;

import com.raidstack.dtos.CadastrarAgendamentoDTO;
import com.raidstack.dtos.VisualizarAgendamentoDTO;
import com.raidstack.services.ICadastrarAgendamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/cadastrar/agendamento")
public class CadastrarAgendamentoController {

    private final ICadastrarAgendamentoService cadastrarAgendamentoService;

    public CadastrarAgendamentoController(ICadastrarAgendamentoService cadastrarAgendamentoService) {
        this.cadastrarAgendamentoService = cadastrarAgendamentoService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permission:AGENDAMENTO_CRIAR')")
    public ResponseEntity<VisualizarAgendamentoDTO> cadastrarAgendamento(@RequestBody @Valid CadastrarAgendamentoDTO agendamento) {
        return ResponseEntity.ok(this.cadastrarAgendamentoService.cadastrarAgendamentoDTO(agendamento));
    }

}
