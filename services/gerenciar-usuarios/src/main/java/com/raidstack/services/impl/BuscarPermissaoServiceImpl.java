package com.raidstack.services.impl;

import com.raidstack.entities.Permissao;
import com.raidstack.repositories.IPermissaoRepository;
import com.raidstack.services.IBuscarPermissaoService;
import com.raidstack.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BuscarPermissaoServiceImpl implements IBuscarPermissaoService {

    @Autowired
    private IPermissaoRepository permissaoRepository;

    public Permissao buscarPermissao(UUID id) {
        return permissaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permissão não encontrada, ID [" + id + "]"));
    }

}
