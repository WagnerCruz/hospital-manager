package com.raidstack.services.impl;

import com.raidstack.dtos.AtualizarUsuarioDTO;
import com.raidstack.dtos.AtualizarUsuarioExternoDTO;
import com.raidstack.dtos.AtualizarUsuarioPerfilDTO;
import com.raidstack.dtos.AtualizarUsuarioSenhaDTO;
import com.raidstack.dtos.VisualizarUsuarioDTO;
import com.raidstack.entities.Perfil;
import com.raidstack.entities.Usuario;
import com.raidstack.enums.PerfilEnum;
import com.raidstack.kafka.events.UsuarioEvent;
import com.raidstack.kafka.producers.KafkaUsuarioProducer;
import com.raidstack.repositories.IUsuarioRepository;
import com.raidstack.services.IBuscarPerfilService;
import com.raidstack.services.IValidarUsuarioService;
import com.raidstack.services.exceptions.ResourceBadRequestException;
import com.raidstack.services.exceptions.ResourceConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("AtualizarUsuarioServiceImpl Tests")
@ExtendWith(MockitoExtension.class)
class AtualizarUsuarioServiceImplTest {

    @Mock
    private IValidarUsuarioService validarUsuarioService;

    @Mock
    private IBuscarPerfilService buscarPerfilService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private KafkaUsuarioProducer kafkaUsuarioProducer;

    @InjectMocks
    private AtualizarUsuarioServiceImpl atualizarUsuarioService;

    private UUID usuarioId;
    private Usuario usuarioExistente;
    private AtualizarUsuarioDTO atualizarUsuarioDTO;
    private VisualizarUsuarioDTO visualizarUsuarioDTO;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        usuarioExistente = criarUsuarioExistente();
        atualizarUsuarioDTO = criarAtualizarUsuarioDTO();
        visualizarUsuarioDTO = criarVisualizarUsuarioDTO();
    }

    @Test
    @DisplayName("Should update user successfully")
    void testAtualizarUsuarioSucesso() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioExistente));
        when(validarUsuarioService.validarCredenciaisUsuario(any(Usuario.class))).thenReturn(new ArrayList<>());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        VisualizarUsuarioDTO resultado = atualizarUsuarioService.atualizarUsuario(atualizarUsuarioDTO);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(validarUsuarioService, times(1)).validarCredenciaisUsuario(any(Usuario.class));
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found in atualizarUsuario")
    void testAtualizarUsuarioUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThrows(ResourceBadRequestException.class, () -> {
            atualizarUsuarioService.atualizarUsuario(atualizarUsuarioDTO);
        });

        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Should throw exception when validation fails in atualizarUsuario")
    void testAtualizarUsuarioErroValidacao() {
        List<String> erros = List.of("Email inválido");

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioExistente));
        when(validarUsuarioService.validarCredenciaisUsuario(any(Usuario.class))).thenReturn(erros);

        assertThrows(ResourceConflictException.class, () -> {
            atualizarUsuarioService.atualizarUsuario(atualizarUsuarioDTO);
        });

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Should update external user successfully")
    void testAtualizarUsuarioExternoSucesso() {
        AtualizarUsuarioExternoDTO atualizarExternoDTO = criarAtualizarUsuarioExternoDTO();
        Perfil perfilPaciente = criarPerfilPaciente();

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioExistente));
        when(validarUsuarioService.validarCredenciaisUsuario(any(Usuario.class))).thenReturn(new ArrayList<>());
        when(validarUsuarioService.validarPerfisUsuario(any(Usuario.class))).thenReturn(new ArrayList<>());
        when(buscarPerfilService.buscarPerfilPorNome(anyString())).thenReturn(perfilPaciente);
        when(passwordEncoder.encode(anyString())).thenReturn("senhaEncodada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        VisualizarUsuarioDTO resultado = atualizarUsuarioService.atualizarUsuarioExterno(atualizarExternoDTO);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(validarUsuarioService, times(1)).validarCredenciaisUsuario(any(Usuario.class));
        verify(validarUsuarioService, times(1)).validarPerfisUsuario(any(Usuario.class));
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(kafkaUsuarioProducer, times(1)).enviarUsuarioAtualizado(any(UsuarioEvent.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found in atualizarUsuarioExterno")
    void testAtualizarUsuarioExternoUsuarioNaoEncontrado() {
        AtualizarUsuarioExternoDTO atualizarExternoDTO = criarAtualizarUsuarioExternoDTO();

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThrows(ResourceBadRequestException.class, () -> {
            atualizarUsuarioService.atualizarUsuarioExterno(atualizarExternoDTO);
        });

        verify(kafkaUsuarioProducer, never()).enviarUsuarioAtualizado(any(UsuarioEvent.class));
    }

    @Test
    @DisplayName("Should throw exception when credential validation fails in atualizarUsuarioExterno")
    void testAtualizarUsuarioExternoErroValidacaoCredenciais() {
        AtualizarUsuarioExternoDTO atualizarExternoDTO = criarAtualizarUsuarioExternoDTO();
        List<String> erros = List.of("Email já cadastrado");

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioExistente));
        when(validarUsuarioService.validarCredenciaisUsuario(any(Usuario.class))).thenReturn(erros);

        assertThrows(ResourceConflictException.class, () -> {
            atualizarUsuarioService.atualizarUsuarioExterno(atualizarExternoDTO);
        });

        verify(kafkaUsuarioProducer, never()).enviarUsuarioAtualizado(any(UsuarioEvent.class));
    }

    @Test
    @DisplayName("Should throw exception when profile validation fails in atualizarUsuarioExterno")
    void testAtualizarUsuarioExternoErroValidacaoPerfil() {
        AtualizarUsuarioExternoDTO atualizarExternoDTO = criarAtualizarUsuarioExternoDTO();
        List<String> errosCredenciais = new ArrayList<>();
        List<String> errosPerfil = List.of("Perfil inválido");

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioExistente));
        when(validarUsuarioService.validarCredenciaisUsuario(any(Usuario.class))).thenReturn(errosCredenciais);
        when(validarUsuarioService.validarPerfisUsuario(any(Usuario.class))).thenReturn(errosPerfil);

        assertThrows(ResourceConflictException.class, () -> {
            atualizarUsuarioService.atualizarUsuarioExterno(atualizarExternoDTO);
        });

        verify(kafkaUsuarioProducer, never()).enviarUsuarioAtualizado(any(UsuarioEvent.class));
    }

    @Test
    @DisplayName("Should update user password successfully")
    void testAtualizarUsuarioSenhaSucesso() {
        String loginUsuario = "usuario@test.com";
        String senhaAtual = "senhaAtual123";
        String novaSenha = "novaSenha123";
        String senhaEncodada = "encodedSenha";

        Usuario usuarioSenha = criarUsuarioComSenha(senhaEncodada);
        AtualizarUsuarioSenhaDTO atualizarSenhaDTO = new AtualizarUsuarioSenhaDTO(
                loginUsuario,
                senhaAtual,
                novaSenha,
                novaSenha
        );

        when(usuarioRepository.findUsuarioByLogin(loginUsuario)).thenReturn(Optional.of(usuarioSenha));
        when(passwordEncoder.matches(senhaAtual, senhaEncodada)).thenReturn(true);
        when(passwordEncoder.encode(novaSenha)).thenReturn("novaSenhaEncodada");

        atualizarUsuarioService.atualizarUsuarioSenha(atualizarSenhaDTO);

        verify(usuarioRepository, times(1)).findUsuarioByLogin(loginUsuario);
        verify(passwordEncoder, times(1)).matches(senhaAtual, senhaEncodada);
        verify(passwordEncoder, times(1)).encode(novaSenha);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found in atualizarUsuarioSenha")
    void testAtualizarUsuarioSenhaUsuarioNaoEncontrado() {
        AtualizarUsuarioSenhaDTO atualizarSenhaDTO = new AtualizarUsuarioSenhaDTO(
                "login",
                "senhaAtual",
                "novaSenha",
                "novaSenha"
        );

        when(usuarioRepository.findUsuarioByLogin("login")).thenReturn(Optional.empty());

        assertThrows(ResourceBadRequestException.class, () -> {
            atualizarUsuarioService.atualizarUsuarioSenha(atualizarSenhaDTO);
        });

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Should throw exception when current password is incorrect")
    void testAtualizarUsuarioSenhaSenhaAtualIncorreta() {
        String loginUsuario = "usuario@test.com";
        String senhaAtual = "senhaAtualErrada";
        String senhaEncodada = "senhaCorretaEncodada";

        Usuario usuarioSenha = criarUsuarioComSenha(senhaEncodada);
        AtualizarUsuarioSenhaDTO atualizarSenhaDTO = new AtualizarUsuarioSenhaDTO(
                loginUsuario,
                senhaAtual,
                "novaSenha",
                "novaSenha"
        );

        when(usuarioRepository.findUsuarioByLogin(loginUsuario)).thenReturn(Optional.of(usuarioSenha));
        when(passwordEncoder.matches(senhaAtual, senhaEncodada)).thenReturn(false);

        assertThrows(ResourceBadRequestException.class, () -> {
            atualizarUsuarioService.atualizarUsuarioSenha(atualizarSenhaDTO);
        });

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Should throw exception when new passwords do not match")
    void testAtualizarUsuarioSenhaNovaSenhasNaoCoincidem() {
        String loginUsuario = "usuario@test.com";
        String senhaAtual = "senhaAtual123";
        String senhaEncodada = "encodedSenha";

        Usuario usuarioSenha = criarUsuarioComSenha(senhaEncodada);
        AtualizarUsuarioSenhaDTO atualizarSenhaDTO = new AtualizarUsuarioSenhaDTO(
                loginUsuario,
                senhaAtual,
                "novaSenha123",
                "novaSenhaErrada"
        );

        when(usuarioRepository.findUsuarioByLogin(loginUsuario)).thenReturn(Optional.of(usuarioSenha));
        when(passwordEncoder.matches(senhaAtual, senhaEncodada)).thenReturn(true);

        assertThrows(ResourceBadRequestException.class, () -> {
            atualizarUsuarioService.atualizarUsuarioSenha(atualizarSenhaDTO);
        });

        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Should update user profile successfully")
    void testAtualizarUsuarioPerfilSucesso() {
        AtualizarUsuarioPerfilDTO atualizarPerfilDTO = criarAtualizarUsuarioPerfilDTO();
        Perfil perfilPaciente = criarPerfilPaciente();

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioExistente));
        when(validarUsuarioService.validarPerfisUsuario(any(Usuario.class))).thenReturn(new ArrayList<>());
        when(buscarPerfilService.buscarPerfilPorNome(anyString())).thenReturn(perfilPaciente);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        VisualizarUsuarioDTO resultado = atualizarUsuarioService.atualizarUsuarioPerfil(atualizarPerfilDTO);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(validarUsuarioService, times(1)).validarPerfisUsuario(any(Usuario.class));
        verify(buscarPerfilService, times(1)).buscarPerfilPorNome(anyString());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found in atualizarUsuarioPerfil")
    void testAtualizarUsuarioPerfilUsuarioNaoEncontrado() {
        AtualizarUsuarioPerfilDTO atualizarPerfilDTO = criarAtualizarUsuarioPerfilDTO();

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThrows(ResourceBadRequestException.class, () -> {
            atualizarUsuarioService.atualizarUsuarioPerfil(atualizarPerfilDTO);
        });

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Should throw exception when profile validation fails in atualizarUsuarioPerfil")
    void testAtualizarUsuarioPerfilErroValidacao() {
        AtualizarUsuarioPerfilDTO atualizarPerfilDTO = criarAtualizarUsuarioPerfilDTO();
        List<String> erros = List.of("Perfil não permitido");

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioExistente));
        when(validarUsuarioService.validarPerfisUsuario(any(Usuario.class))).thenReturn(erros);

        assertThrows(ResourceConflictException.class, () -> {
            atualizarUsuarioService.atualizarUsuarioPerfil(atualizarPerfilDTO);
        });

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Should update user information correctly")
    void testAtualizarInformacoesUsuario() {
        Usuario usuarioAtualizado = criarUsuarioParaAtualizar();
        Usuario usuarioExistente = criarUsuarioExistente();

        ReflectionTestUtils.invokeMethod(
                atualizarUsuarioService,
                "atualizarInformacoesUsuario",
                usuarioAtualizado,
                usuarioExistente
        );

        assertEquals(usuarioAtualizado.getLogin(), usuarioExistente.getLogin());
        assertEquals(usuarioAtualizado.getNome(), usuarioExistente.getNome());
        assertEquals(usuarioAtualizado.getEmail(), usuarioExistente.getEmail());
    }

    @Test
    @DisplayName("Should validate errors and throw exception")
    void testVerificarErrosValidacao() {
        List<String> erros = List.of("Erro 1", "Erro 2");

        assertThrows(ResourceConflictException.class, () -> {
            ReflectionTestUtils.invokeMethod(
                    atualizarUsuarioService,
                    "verificarErrosValidacao",
                    erros
            );
        });
    }

    @Test
    @DisplayName("Should not throw exception when errors list is empty")
    void testVerificarErrosValidacaoVazio() {
        List<String> erros = new ArrayList<>();

        ReflectionTestUtils.invokeMethod(
                atualizarUsuarioService,
                "verificarErrosValidacao",
                erros
        );
    }

    @Test
    @DisplayName("Should update profile IDs correctly")
    void testAtualizarIdsPerfis() {
        List<Perfil> perfis = List.of(criarPerfilSemId());
        Perfil perfilComId = criarPerfilPaciente();

        when(buscarPerfilService.buscarPerfilPorNome(PerfilEnum.PACIENTE.name()))
                .thenReturn(perfilComId);

        ReflectionTestUtils.invokeMethod(
                atualizarUsuarioService,
                "atualizarIdsPerfis",
                perfis
        );

        verify(buscarPerfilService, times(1)).buscarPerfilPorNome(anyString());
        assertEquals(perfilComId.getId(), perfis.get(0).getId());
    }

    @Test
    @DisplayName("Should send kafka notification with temporary password")
    void testEnviarNotificacaoAtualizacaoUsuario() {
        Usuario usuario = criarUsuarioExistente();
        String senhaTemporaria = "senhaTemp123456";

        ReflectionTestUtils.invokeMethod(
                atualizarUsuarioService,
                "enviarNotificacaoAtualizacaoUsuario",
                usuario,
                senhaTemporaria
        );

        verify(kafkaUsuarioProducer, times(1)).enviarUsuarioAtualizado(any(UsuarioEvent.class));
    }

    private Usuario criarUsuarioExistente() {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setLogin("loginAnterior");
        usuario.setNome("Nome Anterior");
        usuario.setEmail("anterior@email.com");
        usuario.setSenha("senhaEncodada");
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setDataAtualizacao(LocalDateTime.now());
        usuario.setPerfis(new ArrayList<>());
        return usuario;
    }

    private AtualizarUsuarioDTO criarAtualizarUsuarioDTO() {
        return new AtualizarUsuarioDTO(
                usuarioId,
                "novoLogin",
                "novoNome",
                "novo@email.com"
        );
    }

    private AtualizarUsuarioExternoDTO criarAtualizarUsuarioExternoDTO() {
        return new AtualizarUsuarioExternoDTO(
                usuarioId,
                "loginExterno",
                "nomeExterno",
                "externo@email.com",
                List.of(PerfilEnum.PACIENTE)
        );
    }

    private AtualizarUsuarioPerfilDTO criarAtualizarUsuarioPerfilDTO() {
        return new AtualizarUsuarioPerfilDTO(
                usuarioId,
                List.of(PerfilEnum.PACIENTE, PerfilEnum.MEDICO)
        );
    }

    private VisualizarUsuarioDTO criarVisualizarUsuarioDTO() {
        return new VisualizarUsuarioDTO(
                usuarioId,
                "login",
                "email@test.com",
                "nome",
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    private Perfil criarPerfilPaciente() {
        Perfil perfil = new Perfil();
        perfil.setId(UUID.randomUUID());
        perfil.setNome(PerfilEnum.PACIENTE.name());
        perfil.setDescricao(PerfilEnum.PACIENTE.getDescricao());
        perfil.setPermissoes(new ArrayList<>());
        return perfil;
    }

    private Perfil criarPerfilSemId() {
        Perfil perfil = new Perfil();
        perfil.setNome(PerfilEnum.PACIENTE.name());
        perfil.setDescricao(PerfilEnum.PACIENTE.getDescricao());
        return perfil;
    }

    private Usuario criarUsuarioParaAtualizar() {
        Usuario usuario = new Usuario();
        usuario.setLogin("novoLogin");
        usuario.setNome("Novo Nome");
        usuario.setEmail("novo@email.com");
        usuario.setDataAtualizacao(LocalDateTime.now());
        return usuario;
    }

    private Usuario criarUsuarioComSenha(String senhaEncodada) {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setLogin("usuario@test.com");
        usuario.setNome("Nome Usuário");
        usuario.setEmail("usuario@test.com");
        usuario.setSenha(senhaEncodada);
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setDataAtualizacao(LocalDateTime.now());
        usuario.setPerfis(new ArrayList<>());
        return usuario;
    }
}
