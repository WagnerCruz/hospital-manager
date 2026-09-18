package com.raidstack.services;

import com.raidstack.entities.Usuario;
import com.raidstack.enums.PerfilEnum;

import java.util.List;
import java.util.UUID;

public interface IValidarUsuarioService {

    List<String> validarCredenciaisUsuario(Usuario usuario);
    List<String> validarPerfisUsuario(Usuario usuario);

    boolean validarPerfilUsuarioPorID(UUID idUsuario, PerfilEnum perfilEnum);

}
