package com.raidstack.services.impl;

import com.raidstack.entities.Permissao;
import com.raidstack.repositories.IPermissaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BuscarPermissaoServiceImpl {

    //// TODO: TROCAR DEPOIS OS RUNTIMEEXCEPTION

    @Autowired
    private IPermissaoRepository permissaoRepository;

    public Permissao buscarPermissao(UUID id) {
        return permissaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permissão não encontrada, ID [" + id + "]"));
    }

}
