package com.raidstack.services;

import com.raidstack.dtos.VisualizarUsuarioDTO;
import com.raidstack.entities.Usuario;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IBuscarUsuarioService {

    Usuario buscarUsuario(UUID id);

    VisualizarUsuarioDTO buscarUsuarioDTO(UUID id);

    Page<VisualizarUsuarioDTO> buscarUsuarios(int page, int size);

}
