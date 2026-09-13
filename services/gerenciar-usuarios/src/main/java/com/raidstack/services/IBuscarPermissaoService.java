package com.raidstack.services;

import com.raidstack.entities.Permissao;

import java.util.UUID;

public interface IBuscarPermissaoService {

    Permissao buscarPermissao(UUID id);

}
