package com.raidstack.services.impl;

import com.raidstack.dtos.AutenticarUsuarioDTO;
import com.raidstack.entities.Usuario;
import com.raidstack.repositories.IUsuarioRepository;
import com.raidstack.services.exceptions.ResourceBadRequestException;
import com.raidstack.services.exceptions.ResourceNotFoundException;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("AutenticarUsuarioServiceImpl - Testes Unitários")
@ExtendWith(MockitoExtension.class)
class AutenticarUsuarioServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IUsuarioRepository usuarioRepository;

    @InjectMocks
    private AutenticarUsuarioServiceImpl autenticarUsuarioService;

    private UUID usuarioId;
    private Usuario usuarioExistente;
    private AutenticarUsuarioDTO autenticarDTO;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        usuarioExistente = criarUsuario();
        autenticarDTO = new AutenticarUsuarioDTO("login@test", "senha123");
    }


    @Test
    @DisplayName("Deve autenticar usuário com sucesso quando credenciais são válidas")
    void testAutenticarUsuarioSucesso() {
        when(usuarioRepository.findByLogin("login@test"))
                .thenReturn(Optional.of(usuarioExistente));
        when(passwordEncoder.matches("senha123", usuarioExistente.getSenha()))
                .thenReturn(true);

        assertDoesNotThrow(() -> autenticarUsuarioService.autenticarUsuario(autenticarDTO));

        verify(usuarioRepository, times(1)).findByLogin("login@test");
        verify(passwordEncoder, times(1)).matches("senha123", usuarioExistente.getSenha());
    }


    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando usuário não existe")
    void testAutenticarUsuarioNaoEncontrado() {
        when(usuarioRepository.findByLogin("login@test"))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> autenticarUsuarioService.autenticarUsuario(autenticarDTO))
                .withMessageContaining("Usuário não encontrado");

        verify(usuarioRepository, times(1)).findByLogin("login@test");
        verify(passwordEncoder, times(0)).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar ResourceBadRequestException quando senha é inválida")
    void testAutenticarUsuarioSenhaInvalida() {
        when(usuarioRepository.findByLogin("login@test"))
                .thenReturn(Optional.of(usuarioExistente));
        when(passwordEncoder.matches("senha123", usuarioExistente.getSenha()))
                .thenReturn(false);

        assertThatExceptionOfType(ResourceBadRequestException.class)
                .isThrownBy(() -> autenticarUsuarioService.autenticarUsuario(autenticarDTO))
                .withMessageContaining("Senha inválida");

        verify(usuarioRepository, times(1)).findByLogin("login@test");
        verify(passwordEncoder, times(1)).matches("senha123", usuarioExistente.getSenha());
    }


    @Test
    @DisplayName("Deve lançar exceção quando login é nulo")
    void testAutenticarUsuarioLoginNulo() {
        AutenticarUsuarioDTO dtoComLoginNulo = new AutenticarUsuarioDTO(null, "senha123");
        when(usuarioRepository.findByLogin(null))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> autenticarUsuarioService.autenticarUsuario(dtoComLoginNulo));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha é nula")
    void testAutenticarUsuarioSenhaNula() {
        AutenticarUsuarioDTO dtoComSenhaNula = new AutenticarUsuarioDTO("login@test", null);
        when(usuarioRepository.findByLogin("login@test"))
                .thenReturn(Optional.of(usuarioExistente));
        when(passwordEncoder.matches(null, usuarioExistente.getSenha()))
                .thenReturn(false);

        assertThatExceptionOfType(ResourceBadRequestException.class)
                .isThrownBy(() -> autenticarUsuarioService.autenticarUsuario(dtoComSenhaNula));
    }


    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setLogin("login@test");
        usuario.setNome("Teste Usuário");
        usuario.setEmail("usuario@test.com");
        usuario.setSenha("senhaEncodada");
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setDataAtualizacao(LocalDateTime.now());
        usuario.setPerfis(new ArrayList<>());
        return usuario;
    }
}
