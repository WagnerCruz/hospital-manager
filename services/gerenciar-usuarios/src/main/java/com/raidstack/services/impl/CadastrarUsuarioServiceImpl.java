package com.raidstack.services.impl;

import com.raidstack.dtos.CadastrarUsuarioDTO;
import com.raidstack.dtos.CadastrarUsuarioExternoDTO;
import com.raidstack.dtos.VisualizarUsuarioDTO;
import com.raidstack.entities.Perfil;
import com.raidstack.entities.Usuario;
import com.raidstack.enums.PerfilEnum;
import com.raidstack.mappers.UsuarioMapper;
import com.raidstack.repositories.IUsuarioRepository;
import com.raidstack.services.IBuscarPerfilService;
import com.raidstack.services.ICadastrarUsuarioService;
import com.raidstack.services.IValidarUsuarioService;
import com.raidstack.services.exceptions.ResourceConflictException;
import com.raidstack.utils.GeradorSenhaTemporaria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CadastrarUsuarioServiceImpl implements ICadastrarUsuarioService {

    private static final PerfilEnum PERFIL_PADRAO_USUARIO = PerfilEnum.PACIENTE;

    private static final int TAMANHO_SENHA_TEMPORARIA = 12;

    @Autowired
    private IValidarUsuarioService validarUsuarioService;

    @Autowired
    private IBuscarPerfilService buscarPerfilService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    public VisualizarUsuarioDTO cadastrarUsuarioDTO(CadastrarUsuarioDTO cadastrarUsuarioDTO) {
        Usuario usuarioNovo = UsuarioMapper.INSTANCE.cadastrarUsuarioDTOToUsuario(cadastrarUsuarioDTO);

        List<String> errosValidacao = new ArrayList<>();
        errosValidacao.addAll(validarUsuarioService.validarCredenciaisUsuario(usuarioNovo));
        this.verificarErrosValidacao(errosValidacao);

        usuarioNovo.setSenha(this.passwordEncoder.encode(usuarioNovo.getSenha()));

        usuarioNovo.setPerfis(Collections.singletonList(buscarPerfilPadrao()));
        Usuario usuarioSalvo = usuarioRepository.save(usuarioNovo);

        return UsuarioMapper.INSTANCE.usuarioToVisualizarUsuarioDTO(usuarioSalvo);
    }

    public VisualizarUsuarioDTO cadastrarUsuarioExternoDTO(CadastrarUsuarioExternoDTO cadastrarUsuarioExternoDTO) {
        Usuario usuarioNovo = UsuarioMapper.INSTANCE.cadastrarUsuarioExternoDTOToUsuario(cadastrarUsuarioExternoDTO);

        List<String> errosValidacao = new ArrayList<>();
        errosValidacao.addAll(validarUsuarioService.validarCredenciaisUsuario(usuarioNovo));
        errosValidacao.addAll(validarUsuarioService.validarPerfisUsuario(usuarioNovo));
        this.verificarErrosValidacao(errosValidacao);

        this.atualizarIdsPerfis(usuarioNovo.getPerfis());

        String senhaTemporaria = GeradorSenhaTemporaria.generate(TAMANHO_SENHA_TEMPORARIA);
        //// TODO: ENVIAR NOTIFICAÇÃO DE SENHA TEMPORÁRIA PARA TROCA PELO SERVIÇO DE NOTIFICAÇÃO
        usuarioNovo.setSenha(this.passwordEncoder.encode(senhaTemporaria));

        Usuario usuarioSalvo = usuarioRepository.save(usuarioNovo);

        return UsuarioMapper.INSTANCE.usuarioToVisualizarUsuarioDTO(usuarioSalvo);
    }

    private Perfil buscarPerfilPadrao() {
        return this.buscarPerfilService.buscarPerfilPorNome(PERFIL_PADRAO_USUARIO.name());
    }

    private void atualizarIdsPerfis(List<Perfil> perfis) {
        perfis.forEach(perfil -> {
            Perfil perfilRetorno = this.buscarPerfilService.buscarPerfilPorNome(perfil.getNome());
            perfil.setId(perfilRetorno.getId());
        });
    }

    private void verificarErrosValidacao(List<String> errosValidacao) {
        if (!errosValidacao.isEmpty()) {
            throw new ResourceConflictException("Erro ao validar Usuário: ", errosValidacao);
        }
    }




}
