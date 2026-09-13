package com.raidstack.services.impl;

import com.raidstack.dtos.AtualizarUsuarioPerfilDTO;
import com.raidstack.dtos.AtualizarUsuarioSenhaDTO;
import com.raidstack.dtos.AtualizarUsuarioDTO;
import com.raidstack.dtos.AtualizarUsuarioExternoDTO;
import com.raidstack.dtos.VisualizarUsuarioDTO;
import com.raidstack.entities.Perfil;
import com.raidstack.entities.Usuario;
import com.raidstack.mappers.UsuarioMapper;
import com.raidstack.repositories.IUsuarioRepository;
import com.raidstack.services.IAtualizarUsuarioService;
import com.raidstack.services.IBuscarPerfilService;
import com.raidstack.services.IValidarUsuarioService;
import com.raidstack.services.exceptions.ResourceBadRequestException;
import com.raidstack.services.exceptions.ResourceConflictException;
import com.raidstack.utils.GeradorSenhaTemporaria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AtualizarUsuarioServiceImpl implements IAtualizarUsuarioService {

    private static final int TAMANHO_SENHA_TEMPORARIA = 12;

    @Autowired
    private IValidarUsuarioService validarUsuarioService;

    @Autowired
    private IBuscarPerfilService buscarPerfilService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    public VisualizarUsuarioDTO atualizarUsuario(AtualizarUsuarioDTO usuarioDTO) {
        Usuario usuario = this.buscarUsuario(usuarioDTO.id());

        Usuario usuarioAtualizado = UsuarioMapper.INSTANCE.atualizarUsuarioDTOToUsuario(usuarioDTO);

        List<String> errosValidacao = new ArrayList<>();
        errosValidacao.addAll(validarUsuarioService.validarCredenciaisUsuario(usuarioAtualizado));
        this.verificarErrosValidacao(errosValidacao);

        usuario.setLogin(usuarioAtualizado.getLogin());
        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setDataAtualizacao(usuarioAtualizado.getDataAtualizacao());

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return UsuarioMapper.INSTANCE.usuarioToVisualizarUsuarioDTO(usuarioSalvo);
    }

    public VisualizarUsuarioDTO atualizarUsuarioExterno(AtualizarUsuarioExternoDTO usuarioDTO) {
        Usuario usuario = this.buscarUsuario(usuarioDTO.id());

        Usuario usuarioAtualizado = UsuarioMapper.INSTANCE.atualizarUsuarioExternoDTOToUsuario(usuarioDTO);

        List<String> errosValidacao = new ArrayList<>();
        errosValidacao.addAll(validarUsuarioService.validarCredenciaisUsuario(usuarioAtualizado));
        errosValidacao.addAll(validarUsuarioService.validarPerfisUsuario(usuarioAtualizado));
        this.verificarErrosValidacao(errosValidacao);

        this.atualizarIdsPerfis(usuarioAtualizado.getPerfis());

        // Informações Fixas
        usuario.setLogin(usuarioAtualizado.getLogin());
        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setDataAtualizacao(usuarioAtualizado.getDataAtualizacao());

        usuario.getPerfis().clear();
        usuario.getPerfis().addAll(usuarioAtualizado.getPerfis());

        String senhaTemporaria = GeradorSenhaTemporaria.generate(TAMANHO_SENHA_TEMPORARIA);
        //// TODO: ENVIAR NOTIFICAÇÃO DE SENHA TEMPORÁRIA PARA TROCA PELO SERVIÇO DE NOTIFICAÇÃO
        usuario.setSenha(this.passwordEncoder.encode(senhaTemporaria));

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return UsuarioMapper.INSTANCE.usuarioToVisualizarUsuarioDTO(usuarioSalvo);
    }

    public void atualizarUsuarioSenha(AtualizarUsuarioSenhaDTO usuarioDTO) {
        Usuario usuario = this.usuarioRepository.findUsuarioByLogin(usuarioDTO.login())
                .orElseThrow(() -> new ResourceBadRequestException("Usuário não encontrado para o login: " + usuarioDTO.login()));

        if (!this.passwordEncoder.matches(usuarioDTO.senhaAtual(), usuario.getSenha())) {
            throw new ResourceBadRequestException("Senha atual incorreta");
        }

        if (!usuarioDTO.novaSenha().equals(usuarioDTO.confirmarSenha())) {
            throw new ResourceBadRequestException("A nova senha e a confirmação de senha não coincidem");
        }

        usuario.setSenha(this.passwordEncoder.encode(usuarioDTO.novaSenha()));
        usuario.setDataAtualizacao(LocalDateTime.now());

        usuarioRepository.save(usuario);
    }

    @Override
    public VisualizarUsuarioDTO atualizarUsuarioPerfil(AtualizarUsuarioPerfilDTO usuarioDTO) {
        Usuario usuario = this.buscarUsuario(usuarioDTO.id());

        Usuario usuarioAtualizado = UsuarioMapper.INSTANCE.atualizarUsuarioPerfilDTOToUsuario(usuarioDTO);

        List<String> errosValidacao = new ArrayList<>();
        errosValidacao.addAll(validarUsuarioService.validarPerfisUsuario(usuarioAtualizado));
        this.verificarErrosValidacao(errosValidacao);

        this.atualizarIdsPerfis(usuarioAtualizado.getPerfis());

        usuario.setDataAtualizacao(LocalDateTime.now());

        usuario.getPerfis().clear();
        usuario.getPerfis().addAll(usuarioAtualizado.getPerfis());

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return UsuarioMapper.INSTANCE.usuarioToVisualizarUsuarioDTO(usuarioSalvo);
    }

    private void verificarErrosValidacao(List<String> errosValidacao) {
        if (!errosValidacao.isEmpty()) {
            throw new ResourceConflictException("Erro ao validar Usuário: ", errosValidacao);
        }
    }

    private Usuario buscarUsuario(UUID idUsuario) {
        Optional<Usuario> usuario = this.usuarioRepository.findById(idUsuario);
        return usuario.orElseThrow(() -> new ResourceBadRequestException("Usuário não encontrado para o ID: " + idUsuario));
    }

    private void atualizarIdsPerfis(List<Perfil> perfis) {
        perfis.forEach(perfil -> {
            Perfil perfilRetorno = this.buscarPerfilService.buscarPerfilPorNome(perfil.getNome());
            perfil.setId(perfilRetorno.getId());
        });
    }

}
