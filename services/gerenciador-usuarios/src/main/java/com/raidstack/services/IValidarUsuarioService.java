package com.raidstack.services;

import com.raidstack.entities.Usuario;

import java.util.List;

public interface IValidarUsuarioService {

    List<String> validarCredenciaisUsuario(Usuario usuario);
    List<String> validarPerfisUsuario(Usuario usuario);

}
