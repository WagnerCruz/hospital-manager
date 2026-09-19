package com.raidstack.controllers;

import com.raidstack.dtos.*;
import com.raidstack.services.IAtualizarAgendamentoService;
import com.raidstack.services.IAtualizarUsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/atualizar/agendamento")
public class AtualizarAgendamentoController {

    @Autowired
    private IAtualizarAgendamentoService atualizarAgendamentoService;

    @PatchMapping
    @PreAuthorize("@validarAcessoProprioUsuarioSecurity.validar(#usuario.id(), authentication)")
    public ResponseEntity<VisualizarAgendamentoDTO> atualizarAgendamento(@RequestBody @Valid AtualizarAgendamentoDTO agendamento) {
        return ResponseEntity.ok(this.atualizarAgendamentoService.atualizarAgendamento(agendamento));
    }

}
