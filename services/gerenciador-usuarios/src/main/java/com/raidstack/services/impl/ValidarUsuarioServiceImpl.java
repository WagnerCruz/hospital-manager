package com.raidstack.services.impl;

import com.raidstack.entities.Perfil;
import com.raidstack.entities.Usuario;
import com.raidstack.enums.PerfilEnum;
import com.raidstack.mappers.UsuarioMapper;
import com.raidstack.repositories.IUsuarioRepository;
import com.raidstack.services.IValidarUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ValidarUsuarioServiceImpl implements IValidarUsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    public List<String> validarCredenciaisUsuario(Usuario usuario) {
        List<String> errosValidacao = new ArrayList<>();
        Optional<Usuario> usuarioLogin = this.usuarioRepository.findUsuarioByLogin(usuario.getLogin());
        Optional<Usuario> usuarioEmail = this.usuarioRepository.findUsuarioByEmail(usuario.getEmail());

        if (usuarioLogin.isPresent() && !usuarioLogin.get().getId().equals(usuario.getId())) {
            errosValidacao.add("login: Login informado já está cadastrado");
        }
        if (usuarioEmail.isPresent() && !usuarioEmail.get().getId().equals(usuario.getId())) {
            errosValidacao.add("email: E-mail informado já está cadastrado");
        }

        return errosValidacao;
    }

    public List<String> validarPerfisUsuario(Usuario usuario) {
        List<String> errosValidacao = new ArrayList<>();
        if (usuario.getPerfis() == null || usuario.getPerfis().isEmpty()) {
            errosValidacao.add("perfis: Usuário deve ter pelo menos um perfil associado");
            return errosValidacao;
        }

        List<PerfilEnum> perfilEnumList = UsuarioMapper.INSTANCE.converterListaPerfilToListaPerfilEnum(usuario.getPerfis());
        if (perfilEnumList.contains(PerfilEnum.MEDICO) && perfilEnumList.contains(PerfilEnum.ENFERMEIRO)) {
            errosValidacao.add("perfis: Usuário não pode ter os perfis MEDICO e ENFERMEIRO ao mesmo tempo");
        }

        return errosValidacao;
    }

    public boolean validarPerfilUsuarioPorID(UUID idUsuario, PerfilEnum perfilEnum) {
        List<Perfil> perfis = this.usuarioRepository.findById(idUsuario).map(Usuario::getPerfis).orElse(Collections.emptyList());
        return perfis.stream().anyMatch(perfil -> perfilEnum.name().equals(perfil.getNome()));
    }


}
