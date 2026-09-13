package com.raidstack.services.impl;

import com.raidstack.entities.Perfil;
import com.raidstack.repositories.IPerfilRepository;
import com.raidstack.services.IBuscarPerfilService;
import com.raidstack.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BuscarPerfilServiceImpl implements IBuscarPerfilService {

    @Autowired
    private IPerfilRepository perfilRepository;

    public Perfil buscarPerfilPorId(UUID id) {
        return perfilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil não encontrado, ID [" + id + "]"));
    }

    public Perfil buscarPerfilPorNome(String nome) {
        return perfilRepository.findByNome(nome)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil não encontrado, NOME [" + nome + "]"));
    }

}
