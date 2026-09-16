package com.raidstack.services.impl;

import com.raidstack.dtos.CadastrarUsuarioDTO;
import com.raidstack.dtos.CadastrarUsuarioExternoDTO;
import com.raidstack.dtos.VisualizarUsuarioDTO;
import com.raidstack.entities.Perfil;
import com.raidstack.entities.Usuario;
import com.raidstack.enums.PerfilEnum;
import com.raidstack.kafka.events.UsuarioEvent;
import com.raidstack.kafka.producers.KafkaUsuarioProducer;
import com.raidstack.mappers.UsuarioMapper;
import com.raidstack.repositories.IUsuarioRepository;
import com.raidstack.services.IBuscarPerfilService;
import com.raidstack.services.IValidarUsuarioService;
import com.raidstack.services.exceptions.ResourceConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("CadastrarUsuarioServiceImpl - Testes Unitários")
@ExtendWith(MockitoExtension.class)
class CadastrarUsuarioServiceImplTest {

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
    private CadastrarUsuarioServiceImpl cadastrarUsuarioService;

    private UUID usuarioId;
    private Usuario usuarioNovo;
    private CadastrarUsuarioDTO cadastrarDTO;
    private Perfil perfilPaciente;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        usuarioNovo = criarUsuario();
        cadastrarDTO = criarCadastrarUsuarioDTO();
        perfilPaciente = criarPerfilPaciente();
    }


    @Test
    @DisplayName("Deve cadastrar usuário comum com sucesso")
    void testCadastrarUsuarioDTOSucesso() {
        when(validarUsuarioService.validarCredenciaisUsuario(any(Usuario.class)))
                .thenReturn(new ArrayList<>());
        when(passwordEncoder.encode(anyString()))
                .thenReturn("senhaEncodada");
        when(buscarPerfilService.buscarPerfilPorNome(PerfilEnum.PACIENTE.name()))
                .thenReturn(perfilPaciente);
        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuarioNovo);

        VisualizarUsuarioDTO resultado = cadastrarUsuarioService.cadastrarUsuarioDTO(cadastrarDTO);

        assertNotNull(resultado);
        verify(validarUsuarioService, times(1)).validarCredenciaisUsuario(any(Usuario.class));
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(buscarPerfilService, times(1)).buscarPerfilPorNome(PerfilEnum.PACIENTE.name());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(kafkaUsuarioProducer, never()).enviarUsuarioCriado(any(UsuarioEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando validação de credenciais falha")
    void testCadastrarUsuarioDTOErroValidacao() {
        List<String> erros = List.of("Email inválido");
        when(validarUsuarioService.validarCredenciaisUsuario(any(Usuario.class)))
                .thenReturn(erros);

        assertThatExceptionOfType(ResourceConflictException.class)
                .isThrownBy(() -> cadastrarUsuarioService.cadastrarUsuarioDTO(cadastrarDTO));

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando perfil padrão não é encontrado")
    void testCadastrarUsuarioDTOPerfilNaoEncontrado() {
        when(validarUsuarioService.validarCredenciaisUsuario(any(Usuario.class)))
                .thenReturn(new ArrayList<>());
        when(passwordEncoder.encode(anyString()))
                .thenReturn("senhaEncodada");
        when(buscarPerfilService.buscarPerfilPorNome(PerfilEnum.PACIENTE.name()))
                .thenThrow(new RuntimeException("Perfil não encontrado"));

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> cadastrarUsuarioService.cadastrarUsuarioDTO(cadastrarDTO));

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }


    @Test
    @DisplayName("Deve cadastrar usuário externo com sucesso e enviar notificação Kafka")
    void testCadastrarUsuarioExternoDTOSucesso() {
        CadastrarUsuarioExternoDTO cadastrarExternoDTO = criarCadastrarUsuarioExternoDTO();

        when(validarUsuarioService.validarCredenciaisUsuario(any(Usuario.class)))
                .thenReturn(new ArrayList<>());
        when(validarUsuarioService.validarPerfisUsuario(any(Usuario.class)))
                .thenReturn(new ArrayList<>());
        when(buscarPerfilService.buscarPerfilPorNome(anyString()))
                .thenReturn(perfilPaciente);
        when(passwordEncoder.encode(anyString()))
                .thenReturn("senhaTemporaria");
        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuarioNovo);

        VisualizarUsuarioDTO resultado = cadastrarUsuarioService.cadastrarUsuarioExternoDTO(cadastrarExternoDTO);

        assertNotNull(resultado);
        verify(validarUsuarioService, times(1)).validarCredenciaisUsuario(any(Usuario.class));
        verify(validarUsuarioService, times(1)).validarPerfisUsuario(any(Usuario.class));
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(kafkaUsuarioProducer, times(1)).enviarUsuarioCriado(any(UsuarioEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando validação de credenciais falha no externo")
    void testCadastrarUsuarioExternoDTOErroValidacaoCredenciais() {
        CadastrarUsuarioExternoDTO cadastrarExternoDTO = criarCadastrarUsuarioExternoDTO();
        List<String> erros = List.of("Email já cadastrado");

        when(validarUsuarioService.validarCredenciaisUsuario(any(Usuario.class)))
                .thenReturn(erros);

        assertThatExceptionOfType(ResourceConflictException.class)
                .isThrownBy(() -> cadastrarUsuarioService.cadastrarUsuarioExternoDTO(cadastrarExternoDTO));

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(kafkaUsuarioProducer, never()).enviarUsuarioCriado(any(UsuarioEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando validação de perfis falha no externo")
    void testCadastrarUsuarioExternoDTOErroValidacaoPerfis() {
        CadastrarUsuarioExternoDTO cadastrarExternoDTO = criarCadastrarUsuarioExternoDTO();
        List<String> errosPerfis = List.of("Perfil não permitido");

        when(validarUsuarioService.validarCredenciaisUsuario(any(Usuario.class)))
                .thenReturn(new ArrayList<>());
        when(validarUsuarioService.validarPerfisUsuario(any(Usuario.class)))
                .thenReturn(errosPerfis);

        assertThatExceptionOfType(ResourceConflictException.class)
                .isThrownBy(() -> cadastrarUsuarioService.cadastrarUsuarioExternoDTO(cadastrarExternoDTO));

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(kafkaUsuarioProducer, never()).enviarUsuarioCriado(any(UsuarioEvent.class));
    }


    @Test
    @DisplayName("Deve cadastrar usuário com email válido")
    void testCadastrarUsuarioEmailValido() {
        CadastrarUsuarioDTO dtoComEmailValido = new CadastrarUsuarioDTO(
                "João",
                "joao@example.com",
                "joao.silva",
                "senha12345"
        );

        when(validarUsuarioService.validarCredenciaisUsuario(any(Usuario.class)))
                .thenReturn(new ArrayList<>());
        when(passwordEncoder.encode(anyString()))
                .thenReturn("senhaEncodada");
        when(buscarPerfilService.buscarPerfilPorNome(PerfilEnum.PACIENTE.name()))
                .thenReturn(perfilPaciente);
        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuarioNovo);

        VisualizarUsuarioDTO resultado = cadastrarUsuarioService.cadastrarUsuarioDTO(dtoComEmailValido);

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Deve cadastrar usuário com nome contendo caracteres especiais")
    void testCadastrarUsuarioNomeComEspacos() {
        CadastrarUsuarioDTO dtoComNomeEspecial = new CadastrarUsuarioDTO(
                "João da Silva",
                "joao@example.com",
                "joao.silva",
                "senha12345"
        );

        when(validarUsuarioService.validarCredenciaisUsuario(any(Usuario.class)))
                .thenReturn(new ArrayList<>());
        when(passwordEncoder.encode(anyString()))
                .thenReturn("senhaEncodada");
        when(buscarPerfilService.buscarPerfilPorNome(PerfilEnum.PACIENTE.name()))
                .thenReturn(perfilPaciente);
        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuarioNovo);

        VisualizarUsuarioDTO resultado = cadastrarUsuarioService.cadastrarUsuarioDTO(dtoComNomeEspecial);

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Deve gerar senha temporária com tamanho correto para usuário externo")
    void testSenhaTemporariaComTamanhoCorreto() {
        CadastrarUsuarioExternoDTO cadastrarExternoDTO = criarCadastrarUsuarioExternoDTO();

        when(validarUsuarioService.validarCredenciaisUsuario(any(Usuario.class)))
                .thenReturn(new ArrayList<>());
        when(validarUsuarioService.validarPerfisUsuario(any(Usuario.class)))
                .thenReturn(new ArrayList<>());
        when(buscarPerfilService.buscarPerfilPorNome(anyString()))
                .thenReturn(perfilPaciente);
        when(passwordEncoder.encode(anyString()))
                .thenReturn("senhaEncodada");
        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuarioNovo);

        cadastrarUsuarioService.cadastrarUsuarioExternoDTO(cadastrarExternoDTO);

        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando lista de erros de validação não está vazia")
    void testErrosValidacaoNaoVazio() {
        List<String> erros = List.of("Erro 1", "Erro 2", "Erro 3");

        when(validarUsuarioService.validarCredenciaisUsuario(any(Usuario.class)))
                .thenReturn(erros);

        assertThatExceptionOfType(ResourceConflictException.class)
                .isThrownBy(() -> cadastrarUsuarioService.cadastrarUsuarioDTO(cadastrarDTO))
                .withMessageContaining("Erro ao validar Usuário");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }


    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setLogin("joao.silva");
        usuario.setNome("João Silva");
        usuario.setEmail("joao@example.com");
        usuario.setSenha("senhaEncodada");
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setDataAtualizacao(LocalDateTime.now());
        usuario.setPerfis(new ArrayList<>());
        return usuario;
    }

    private CadastrarUsuarioDTO criarCadastrarUsuarioDTO() {
        return new CadastrarUsuarioDTO(
                "João Silva",
                "joao@example.com",
                "joao.silva",
                "senha12345"
        );
    }

    private CadastrarUsuarioExternoDTO criarCadastrarUsuarioExternoDTO() {
        return new CadastrarUsuarioExternoDTO(
                "João Silva",
                "joao@example.com",
                "joao.silva",
                List.of(PerfilEnum.PACIENTE, PerfilEnum.MEDICO)
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
}
