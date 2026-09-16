package com.raidstack.services.impl;

import com.raidstack.dtos.VisualizarUsuarioDTO;
import com.raidstack.entities.Usuario;
import com.raidstack.mappers.UsuarioMapper;
import com.raidstack.repositories.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("BuscarUsuarioServiceImpl - Testes Unitários")
@ExtendWith(MockitoExtension.class)
class BuscarUsuarioServiceImplTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    @InjectMocks
    private BuscarUsuarioServiceImpl buscarUsuarioService;

    private UUID usuarioId;
    private Usuario usuarioExistente;
    private VisualizarUsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        usuarioExistente = criarUsuario();
        usuarioDTO = criarUsuarioDTO();
    }


    @Test
    @DisplayName("Deve encontrar usuário por ID com sucesso")
    void testBuscarUsuarioSucesso() {
        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuarioExistente));

        Usuario resultado = buscarUsuarioService.buscarUsuario(usuarioId);

        assertNotNull(resultado);
        assertThat(resultado.getId()).isEqualTo(usuarioId);
        assertThat(resultado.getLogin()).isEqualTo("login@test");
        verify(usuarioRepository, times(1)).findById(usuarioId);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando usuário não é encontrado por ID")
    void testBuscarUsuarioNaoEncontrado() {
        UUID idInexistente = UUID.randomUUID();
        when(usuarioRepository.findById(idInexistente))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> buscarUsuarioService.buscarUsuario(idInexistente))
                .withMessageContaining("Usuário não encontrado")
                .withMessageContaining(idInexistente.toString());

        verify(usuarioRepository, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void testBuscarUsuarioIdNulo() {
        when(usuarioRepository.findById(null))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> buscarUsuarioService.buscarUsuario(null));
    }


    @Test
    @DisplayName("Deve buscar usuário DTO com sucesso")
    void testBuscarUsuarioDTOSucesso() {
        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuarioExistente));

        VisualizarUsuarioDTO resultado = buscarUsuarioService.buscarUsuarioDTO(usuarioId);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).findById(usuarioId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar DTO quando usuário não existe")
    void testBuscarUsuarioDTONaoEncontrado() {
        UUID idInexistente = UUID.randomUUID();
        when(usuarioRepository.findById(idInexistente))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> buscarUsuarioService.buscarUsuarioDTO(idInexistente));
    }


    @Test
    @DisplayName("Deve buscar lista paginada de usuários com sucesso")
    void testBuscarUsuariosPaginaSucesso() {
        List<Usuario> usuarios = List.of(usuarioExistente);
        Page<Usuario> usuarioPage = new PageImpl<>(usuarios);

        when(usuarioRepository.findAll(any(Pageable.class)))
                .thenReturn(usuarioPage);

        Page<VisualizarUsuarioDTO> resultado = buscarUsuarioService.buscarUsuarios(1, 10);

        assertNotNull(resultado);
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        verify(usuarioRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum usuário existe")
    void testBuscarUsuariosListaVazia() {
        Page<Usuario> usuarioPageVazia = new PageImpl<>(new ArrayList<>());

        when(usuarioRepository.findAll(any(Pageable.class)))
                .thenReturn(usuarioPageVazia);

        Page<VisualizarUsuarioDTO> resultado = buscarUsuarioService.buscarUsuarios(1, 10);

        assertNotNull(resultado);
        assertThat(resultado.getTotalElements()).isZero();
        assertThat(resultado.getContent()).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar múltiplas páginas com sucesso")
    void testBuscarUsuariosMultiplasPaginas() {
        List<Usuario> usuarios = List.of(usuarioExistente, criarOutroUsuario());
        Page<Usuario> usuarioPage = new PageImpl<>(usuarios, PageRequest.of(0, 2), 2);

        when(usuarioRepository.findAll(any(Pageable.class)))
                .thenReturn(usuarioPage);

        Page<VisualizarUsuarioDTO> resultado = buscarUsuarioService.buscarUsuarios(1, 2);

        assertNotNull(resultado);
        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getContent()).hasSize(2);
    }


    @Test
    @DisplayName("Deve encontrar usuário por email com sucesso")
    void testBuscarUsuarioPorEmailSucesso() {
        String email = "usuario@test.com";
        when(usuarioRepository.findUsuarioByEmail(email))
                .thenReturn(Optional.of(usuarioExistente));

        VisualizarUsuarioDTO resultado = buscarUsuarioService.buscarUsuarioPorEmail(email);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).findUsuarioByEmail(email);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando usuário não é encontrado por email")
    void testBuscarUsuarioPorEmailNaoEncontrado() {
        String emailInexistente = "inexistente@test.com";
        when(usuarioRepository.findUsuarioByEmail(emailInexistente))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> buscarUsuarioService.buscarUsuarioPorEmail(emailInexistente))
                .withMessageContaining("Usuário não encontrado")
                .withMessageContaining(emailInexistente);

        verify(usuarioRepository, times(1)).findUsuarioByEmail(emailInexistente);
    }

    @Test
    @DisplayName("Deve lançar exceção quando email é nulo")
    void testBuscarUsuarioPorEmailNulo() {
        when(usuarioRepository.findUsuarioByEmail(null))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> buscarUsuarioService.buscarUsuarioPorEmail(null));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email é vazio")
    void testBuscarUsuarioPorEmailVazio() {
        when(usuarioRepository.findUsuarioByEmail(""))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> buscarUsuarioService.buscarUsuarioPorEmail(""));
    }


    @Test
    @DisplayName("Deve encontrar usuário por nome com sucesso")
    void testBuscarUsuarioPorNomeSucesso() {
        String nome = "João Silva";
        when(usuarioRepository.findUsuarioByNome(nome))
                .thenReturn(Optional.of(usuarioExistente));

        VisualizarUsuarioDTO resultado = buscarUsuarioService.buscarUsuarioPorNome(nome);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).findUsuarioByNome(nome);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando usuário não é encontrado por nome")
    void testBuscarUsuarioPorNomeNaoEncontrado() {
        String nomeInexistente = "Nome Inexistente";
        when(usuarioRepository.findUsuarioByNome(nomeInexistente))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> buscarUsuarioService.buscarUsuarioPorNome(nomeInexistente))
                .withMessageContaining("Usuário não encontrado")
                .withMessageContaining(nomeInexistente);

        verify(usuarioRepository, times(1)).findUsuarioByNome(nomeInexistente);
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é nulo")
    void testBuscarUsuarioPorNomeNulo() {
        when(usuarioRepository.findUsuarioByNome(null))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> buscarUsuarioService.buscarUsuarioPorNome(null));
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é vazio")
    void testBuscarUsuarioPorNomeVazio() {
        when(usuarioRepository.findUsuarioByNome(""))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> buscarUsuarioService.buscarUsuarioPorNome(""));
    }



    @Test
    @DisplayName("Deve calcular corretamente o índice da página")
    void testCalculoIndicePagina() {
        Page<Usuario> usuarioPage = new PageImpl<>(new ArrayList<>());

        when(usuarioRepository.findAll(PageRequest.of(2, 5)))
                .thenReturn(usuarioPage);

        buscarUsuarioService.buscarUsuarios(3, 5);

        verify(usuarioRepository, times(1)).findAll(PageRequest.of(2, 5));
    }

    @Test
    @DisplayName("Deve buscar usuários com tamanho de página pequeno")
    void testBuscarUsuariosTamanhoPaginaPequeno() {
        List<Usuario> usuarios = List.of(usuarioExistente);
        Page<Usuario> usuarioPage = new PageImpl<>(usuarios);

        when(usuarioRepository.findAll(any(Pageable.class)))
                .thenReturn(usuarioPage);

        Page<VisualizarUsuarioDTO> resultado = buscarUsuarioService.buscarUsuarios(1, 1);

        assertNotNull(resultado);
        assertThat(resultado.getSize()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Deve buscar usuários com tamanho de página grande")
    void testBuscarUsuariosTamanhoPaginaGrande() {
        List<Usuario> usuarios = List.of(usuarioExistente);
        Page<Usuario> usuarioPage = new PageImpl<>(usuarios);

        when(usuarioRepository.findAll(any(Pageable.class)))
                .thenReturn(usuarioPage);

        Page<VisualizarUsuarioDTO> resultado = buscarUsuarioService.buscarUsuarios(1, 100);

        assertNotNull(resultado);
        assertThat(resultado.getSize()).isGreaterThanOrEqualTo(0);
    }


    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setLogin("login@test");
        usuario.setNome("João Silva");
        usuario.setEmail("usuario@test.com");
        usuario.setSenha("senhaEncodada");
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setDataAtualizacao(LocalDateTime.now());
        usuario.setPerfis(new ArrayList<>());
        return usuario;
    }

    private Usuario criarOutroUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setLogin("outro_login");
        usuario.setNome("Maria Santos");
        usuario.setEmail("maria@test.com");
        usuario.setSenha("senhaEncodada");
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setDataAtualizacao(LocalDateTime.now());
        usuario.setPerfis(new ArrayList<>());
        return usuario;
    }

    private VisualizarUsuarioDTO criarUsuarioDTO() {
        return new VisualizarUsuarioDTO(
                usuarioId,
                "login@test",
                "usuario@test.com",
                "João Silva",
                true,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }
}
