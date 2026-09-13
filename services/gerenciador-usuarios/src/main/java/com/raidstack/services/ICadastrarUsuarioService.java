package com.raidstack.services;

import com.raidstack.dtos.CadastrarUsuarioDTO;
import com.raidstack.dtos.CadastrarUsuarioExternoDTO;
import com.raidstack.dtos.VisualizarUsuarioDTO;

public interface ICadastrarUsuarioService {

    VisualizarUsuarioDTO cadastrarUsuarioDTO(CadastrarUsuarioDTO cadastrarUsuarioDTO);

    VisualizarUsuarioDTO cadastrarUsuarioExternoDTO(CadastrarUsuarioExternoDTO cadastrarUsuarioExternoDTO);

}
