package com.raidstack.services.impl;

import com.raidstack.entities.Usuario;
import com.raidstack.enums.PerfilEnum;
import com.raidstack.repositories.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("ValidarUsuarioServiceImpl - Testes Unitários")
@ExtendWith(MockitoExtension.class)
class ValidarUsuarioServiceImplTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    @InjectMocks
    private ValidarUsuarioServiceImpl validarUsuarioService;

    private UUID usuarioId;
    private Usuario usuarioExistente;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        usuarioExistente = criarUsuarioExistente();
    }


    @Test
    @DisplayName("Deve retornar lista vazia quando credenciais são válidas")
    void testValidarCredenciaisUsuarioValidas() {
        Usuario usuarioNovo = criarUsuarioNovo();

        when(usuarioRepository.findByLogin(usuarioNovo.getLogin()))
                .thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(usuarioNovo.getEmail()))
                .thenReturn(Optional.empty());

        List<String> erros = validarUsuarioService.validarCredenciaisUsuario(usuarioNovo);

        assertThat(erros).isEmpty();
        verify(usuarioRepository, times(1)).findByLogin(usuarioNovo.getLogin());
        verify(usuarioRepository, times(1)).findByEmail(usuarioNovo.getEmail());
    }

    @Test
    @DisplayName("Deve retornar erro quando login já existe e IDs são diferentes")
    void testValidarCredenciaisLoginDuplicado() {
        Usuario usuarioNovo = criarUsuarioNovo();
        Usuario usuarioComLoginExistente = criarUsuarioComDadosEspecificos("loginexistente", "outro@email.com", UUID.randomUUID());

        when(usuarioRepository.findByLogin(usuarioNovo.getLogin()))
                .thenReturn(Optional.of(usuarioComLoginExistente));
        when(usuarioRepository.findByEmail(usuarioNovo.getEmail()))
                .thenReturn(Optional.empty());

        List<String> erros = validarUsuarioService.validarCredenciaisUsuario(usuarioNovo);

        assertThat(erros).isNotEmpty();
        assertThat(erros).anyMatch(e -> e.contains("login"));
        assertThat(erros).anyMatch(e -> e.contains("Login informado já está cadastrado"));
    }

    @Test
    @DisplayName("Deve retornar erro quando email já existe e IDs são diferentes")
    void testValidarCredenciaisEmailDuplicado() {
        Usuario usuarioNovo = criarUsuarioNovo();
        Usuario usuarioComEmailExistente = criarUsuarioComDadosEspecificos("outroLogin", "email@example.com", UUID.randomUUID());

        when(usuarioRepository.findByLogin(usuarioNovo.getLogin()))
                .thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(usuarioNovo.getEmail()))
                .thenReturn(Optional.of(usuarioComEmailExistente));

        List<String> erros = validarUsuarioService.validarCredenciaisUsuario(usuarioNovo);

        assertThat(erros).isNotEmpty();
        assertThat(erros).anyMatch(e -> e.contains("email"));
        assertThat(erros).anyMatch(e -> e.contains("E-mail informado já está cadastrado"));
    }

    @Test
    @DisplayName("Deve retornar dois erros quando login e email já existem e IDs são diferentes")
    void testValidarCredenciaisLoginEmailDuplicados() {
        Usuario usuarioNovo = criarUsuarioNovo();
        UUID outroId = UUID.randomUUID();
        
        Usuario usuarioComLoginExistente = criarUsuarioComDadosEspecificos("loginexistente", "outro@email.com", outroId);
        Usuario usuarioComEmailExistente = criarUsuarioComDadosEspecificos("outroLogin", "email@example.com", outroId);

        when(usuarioRepository.findByLogin(usuarioNovo.getLogin()))
                .thenReturn(Optional.of(usuarioComLoginExistente));
        when(usuarioRepository.findByEmail(usuarioNovo.getEmail()))
                .thenReturn(Optional.of(usuarioComEmailExistente));

        List<String> erros = validarUsuarioService.validarCredenciaisUsuario(usuarioNovo);

        assertThat(erros).hasSize(2);
    }

    @Test
    @DisplayName("Deve ignorar login duplicado quando ID é igual (atualização)")
    void testValidarCredenciaisLoginDuplicadoMesmoId() {
        Usuario usuarioExistente = criarUsuarioExistente();

        when(usuarioRepository.findByLogin(usuarioExistente.getLogin()))
                .thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.findByEmail(usuarioExistente.getEmail()))
                .thenReturn(Optional.empty());

        List<String> erros = validarUsuarioService.validarCredenciaisUsuario(usuarioExistente);

        assertThat(erros).isEmpty();
    }

    @Test
    @DisplayName("Deve ignorar email duplicado quando ID é igual (atualização)")
    void testValidarCredenciaisEmailDuplicadoMesmoId() {
        Usuario usuarioExistente = criarUsuarioExistente();

        when(usuarioRepository.findByLogin(usuarioExistente.getLogin()))
                .thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(usuarioExistente.getEmail()))
                .thenReturn(Optional.of(usuarioExistente));

        List<String> erros = validarUsuarioService.validarCredenciaisUsuario(usuarioExistente);

        assertThat(erros).isEmpty();
    }


    @Test
    @DisplayName("Deve retornar lista vazia quando perfis são válidos")
    void testValidarPerfisUsuarioValido() {
        Usuario usuarioComPerfil = criarUsuarioComPerfis(PerfilEnum.PACIENTE);

        List<String> erros = validarUsuarioService.validarPerfisUsuario(usuarioComPerfil);

        assertThat(erros).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar erro quando usuário não tem perfis associados")
    void testValidarPerfisUsuarioSemPerfis() {
        Usuario usuarioSemPerfil = criarUsuarioComPerfis();

        List<String> erros = validarUsuarioService.validarPerfisUsuario(usuarioSemPerfil);

        assertThat(erros).isNotEmpty();
        assertThat(erros).anyMatch(e -> e.contains("Usuário deve ter pelo menos um perfil"));
    }

    @Test
    @DisplayName("Deve retornar erro quando perfis é nulo")
    void testValidarPerfisUsuarioPerfisNulo() {
        Usuario usuario = criarUsuarioComPerfis();
        usuario.setPerfis(null);

        List<String> erros = validarUsuarioService.validarPerfisUsuario(usuario);

        assertThat(erros).isNotEmpty();
        assertThat(erros).anyMatch(e -> e.contains("Usuário deve ter pelo menos um perfil"));
    }

    @Test
    @DisplayName("Deve retornar erro quando usuário tem perfis MEDICO e ENFERMEIRO simultaneamente")
    void testValidarPerfisUsuarioMedicoEnfermeiro() {
        Usuario usuarioComPerfisInvalidos = criarUsuarioComPerfis(PerfilEnum.MEDICO, PerfilEnum.ENFERMEIRO);

        List<String> erros = validarUsuarioService.validarPerfisUsuario(usuarioComPerfisInvalidos);

        assertThat(erros).isNotEmpty();
        assertThat(erros).anyMatch(e -> e.contains("MEDICO") && e.contains("ENFERMEIRO"));
    }

    @Test
    @DisplayName("Deve permitir perfil MEDICO sozinho")
    void testValidarPerfisUsuarioApenaMedico() {
        Usuario usuarioMedico = criarUsuarioComPerfis(PerfilEnum.MEDICO);

        List<String> erros = validarUsuarioService.validarPerfisUsuario(usuarioMedico);

        assertThat(erros).isEmpty();
    }

    @Test
    @DisplayName("Deve permitir perfil ENFERMEIRO sozinho")
    void testValidarPerfisUsuarioApenasEnfermeiro() {
        Usuario usuarioEnfermeiro = criarUsuarioComPerfis(PerfilEnum.ENFERMEIRO);

        List<String> erros = validarUsuarioService.validarPerfisUsuario(usuarioEnfermeiro);

        assertThat(erros).isEmpty();
    }

    @Test
    @DisplayName("Deve permitir perfis MEDICO e ADMINISTRADOR juntos")
    void testValidarPerfisUsuarioMedicoAdministrador() {
        Usuario usuarioComPerfisValidos = criarUsuarioComPerfis(PerfilEnum.MEDICO, PerfilEnum.ADMINISTRADOR);

        List<String> erros = validarUsuarioService.validarPerfisUsuario(usuarioComPerfisValidos);

        assertThat(erros).isEmpty();
    }

    @Test
    @DisplayName("Deve permitir perfis ENFERMEIRO e PACIENTE juntos")
    void testValidarPerfisUsuarioEnfermeiroePaciente() {
        Usuario usuarioComPerfisValidos = criarUsuarioComPerfis(PerfilEnum.ENFERMEIRO, PerfilEnum.PACIENTE);

        List<String> erros = validarUsuarioService.validarPerfisUsuario(usuarioComPerfisValidos);

        assertThat(erros).isEmpty();
    }


    @Test
    @DisplayName("Deve validar credenciais com login contendo números")
    void testValidarCredenciaisLoginComNumeros() {
        Usuario usuarioComNumeros = criarUsuarioNovo();
        usuarioComNumeros.setLogin("usuario123");

        when(usuarioRepository.findByLogin("usuario123"))
                .thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(usuarioComNumeros.getEmail()))
                .thenReturn(Optional.empty());

        List<String> erros = validarUsuarioService.validarCredenciaisUsuario(usuarioComNumeros);

        assertThat(erros).isEmpty();
    }

    @Test
    @DisplayName("Deve validar credenciais com email contendo pontos e hífen")
    void testValidarCredenciaisEmailComPontosHifen() {
        Usuario usuarioComEmail = criarUsuarioNovo();
        usuarioComEmail.setEmail("usuario.teste-123@example.com");

        when(usuarioRepository.findByLogin(usuarioComEmail.getLogin()))
                .thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("usuario.teste-123@example.com"))
                .thenReturn(Optional.empty());

        List<String> erros = validarUsuarioService.validarCredenciaisUsuario(usuarioComEmail);

        assertThat(erros).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar múltiplos erros em lista como esperado")
    void testValidarPerfisRetornaListaComMultiplosErros() {
        Usuario usuarioSemPerfis = criarUsuarioComPerfis();

        List<String> erros = validarUsuarioService.validarPerfisUsuario(usuarioSemPerfis);

        assertThat(erros).isNotEmpty();
        assertThat(erros).isInstanceOf(List.class);
    }


    private Usuario criarUsuarioExistente() {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setLogin("loginexistente");
        usuario.setNome("João Silva");
        usuario.setEmail("email@example.com");
        usuario.setSenha("senhaEncodada");
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setDataAtualizacao(LocalDateTime.now());
        usuario.setPerfis(new ArrayList<>());
        return usuario;
    }

    private Usuario criarUsuarioNovo() {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setLogin("loginexistente");
        usuario.setNome("Novo Usuário");
        usuario.setEmail("email@example.com");
        usuario.setSenha("senhaEncodada");
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setDataAtualizacao(LocalDateTime.now());
        usuario.setPerfis(new ArrayList<>());
        return usuario;
    }

    private Usuario criarUsuarioComDadosEspecificos(String login, String email, UUID id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setLogin(login);
        usuario.setNome("Usuário Teste");
        usuario.setEmail(email);
        usuario.setSenha("senhaEncodada");
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setDataAtualizacao(LocalDateTime.now());
        usuario.setPerfis(new ArrayList<>());
        return usuario;
    }

    private Usuario criarUsuarioComPerfis(PerfilEnum... perfis) {
        Usuario usuario = criarUsuarioExistente();
        List<com.raidstack.entities.Perfil> listaPerfi = new ArrayList<>();

        for (PerfilEnum perfilEnum : perfis) {
            com.raidstack.entities.Perfil perfil = new com.raidstack.entities.Perfil();
            perfil.setId(UUID.randomUUID());
            perfil.setNome(perfilEnum.name());
            perfil.setDescricao(perfilEnum.getDescricao());
            listaPerfi.add(perfil);
        }

        usuario.setPerfis(listaPerfi);
        return usuario;
    }
}
