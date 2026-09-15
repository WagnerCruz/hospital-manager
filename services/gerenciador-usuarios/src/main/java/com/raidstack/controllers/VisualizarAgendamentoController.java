package com.raidstack.controllers;

import com.raidstack.dtos.VisualizarAgendamentoDTO;
import com.raidstack.services.IBuscarAgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/visualizar")
public class VisualizarAgendamentoController {

    @Autowired
    private IBuscarAgendamentoService buscarAgendamentoService;

    @GetMapping("/{id}")
    public ResponseEntity<VisualizarAgendamentoDTO> buscarAgendamentoPorID(@PathVariable String id) {
        return ResponseEntity.ok(this.buscarAgendamentoService.buscarAgendamentoDTO(UUID.fromString(id)));
    }

    @GetMapping
    public ResponseEntity<Page<VisualizarAgendamentoDTO>> buscarAgendamento(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        return ResponseEntity.ok(this.buscarAgendamentoService.buscarAgendamentos(page, size));
    }

}
