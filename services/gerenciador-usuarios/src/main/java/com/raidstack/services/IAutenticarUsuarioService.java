package com.raidstack.services;

import com.raidstack.dtos.AutenticarUsuarioDTO;

public interface IAutenticarUsuarioService {

    String autenticarUsuario(AutenticarUsuarioDTO usuario);

}
