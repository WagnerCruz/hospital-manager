package com.raidstack.services;

import com.raidstack.entities.Perfil;

import java.util.UUID;

public interface IBuscarPerfilService {

    Perfil buscarPerfilPorId(UUID ID);
    Perfil buscarPerfilPorNome(String nome);

}
