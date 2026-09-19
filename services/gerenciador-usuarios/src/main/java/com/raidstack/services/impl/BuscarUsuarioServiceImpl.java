package com.raidstack.services.impl;

import com.raidstack.dtos.VisualizarUsuarioDTO;
import com.raidstack.entities.Usuario;
import com.raidstack.mappers.UsuarioMapper;
import com.raidstack.repositories.IUsuarioRepository;
import com.raidstack.services.IBuscarUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BuscarUsuarioServiceImpl implements IBuscarUsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    public Usuario buscarUsuario(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado, ID [" + id + "]"));
    }

    public VisualizarUsuarioDTO buscarUsuarioDTO(UUID id) {
        Usuario usuario = buscarUsuario(id);
        return UsuarioMapper.INSTANCE.usuarioToVisualizarUsuarioDTO(usuario);
    }

    @Override
    public Page<VisualizarUsuarioDTO> buscarUsuarios(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Usuario> usuariosPage = this.usuarioRepository.findAll(pageable);
        return UsuarioMapper.INSTANCE.usuarioToVisualizarUsuarioDTO(usuariosPage);
    }

    @Override
    public VisualizarUsuarioDTO buscarUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado, email [" + email + "]"));
        return UsuarioMapper.INSTANCE.usuarioToVisualizarUsuarioDTO(usuario);
    }

    @Override
    public VisualizarUsuarioDTO buscarUsuarioPorNome(String nome) {
        Usuario usuario = usuarioRepository.findByNome(nome)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado, nome [" + nome + "]"));
        return UsuarioMapper.INSTANCE.usuarioToVisualizarUsuarioDTO(usuario);
    }

}
