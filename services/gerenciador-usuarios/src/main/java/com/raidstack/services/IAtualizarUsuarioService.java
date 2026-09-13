package com.raidstack.services;

import com.raidstack.dtos.AtualizarUsuarioDTO;
import com.raidstack.dtos.AtualizarUsuarioExternoDTO;
import com.raidstack.dtos.AtualizarUsuarioPerfilDTO;
import com.raidstack.dtos.AtualizarUsuarioSenhaDTO;
import com.raidstack.dtos.VisualizarUsuarioDTO;

public interface IAtualizarUsuarioService {

    VisualizarUsuarioDTO atualizarUsuario(AtualizarUsuarioDTO usuarioDTO);

    VisualizarUsuarioDTO atualizarUsuarioExterno(AtualizarUsuarioExternoDTO usuarioDTO);

    void atualizarUsuarioSenha(AtualizarUsuarioSenhaDTO usuarioDTO);

    VisualizarUsuarioDTO atualizarUsuarioPerfil(AtualizarUsuarioPerfilDTO usuarioDTO);

}
