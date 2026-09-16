package com.raidstack.services.impl;

import com.raidstack.entities.Permissao;
import com.raidstack.repositories.IPermissaoRepository;
import com.raidstack.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("BuscarPermissaoServiceImpl - Testes Unitários")
@ExtendWith(MockitoExtension.class)
class BuscarPermissaoServiceImplTest {

    @Mock
    private IPermissaoRepository permissaoRepository;

    @InjectMocks
    private BuscarPermissaoServiceImpl buscarPermissaoService;

    private UUID permissaoId;
    private Permissao permissaoExistente;

    @BeforeEach
    void setUp() {
        permissaoId = UUID.randomUUID();
        permissaoExistente = criarPermissao();
    }


    @Test
    @DisplayName("Deve encontrar permissão por ID com sucesso")
    void testBuscarPermissaoSucesso() {
        when(permissaoRepository.findById(permissaoId))
                .thenReturn(Optional.of(permissaoExistente));

        Permissao resultado = buscarPermissaoService.buscarPermissao(permissaoId);

        assertNotNull(resultado);
        assertThat(resultado.getId()).isEqualTo(permissaoId);
        assertThat(resultado.getNome()).isEqualTo("LEITURA");
        verify(permissaoRepository, times(1)).findById(permissaoId);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando permissão não é encontrada")
    void testBuscarPermissaoNaoEncontrada() {
        UUID idInexistente = UUID.randomUUID();
        when(permissaoRepository.findById(idInexistente))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> buscarPermissaoService.buscarPermissao(idInexistente))
                .withMessageContaining("Permissão não encontrada")
                .withMessageContaining("ID")
                .withMessageContaining(idInexistente.toString());

        verify(permissaoRepository, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void testBuscarPermissaoIdNulo() {
        when(permissaoRepository.findById(null))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> buscarPermissaoService.buscarPermissao(null));
    }

    @Test
    @DisplayName("Deve encontrar permissão múltiplas vezes com mesmo ID")
    void testBuscarPermissaoMultiplasChamadas() {
        when(permissaoRepository.findById(permissaoId))
                .thenReturn(Optional.of(permissaoExistente));

        Permissao resultado1 = buscarPermissaoService.buscarPermissao(permissaoId);
        Permissao resultado2 = buscarPermissaoService.buscarPermissao(permissaoId);

        assertThat(resultado1.getId()).isEqualTo(resultado2.getId());
        verify(permissaoRepository, times(2)).findById(permissaoId);
    }

    @Test
    @DisplayName("Deve encontrar diferentes permissões com IDs diferentes")
    void testBuscarPermissoesDiferentes() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Permissao permissao1 = criarPermissaoComIdENome(id1, "LEITURA");
        Permissao permissao2 = criarPermissaoComIdENome(id2, "ESCRITA");

        when(permissaoRepository.findById(id1))
                .thenReturn(Optional.of(permissao1));
        when(permissaoRepository.findById(id2))
                .thenReturn(Optional.of(permissao2));

        Permissao resultado1 = buscarPermissaoService.buscarPermissao(id1);
        Permissao resultado2 = buscarPermissaoService.buscarPermissao(id2);

        assertThat(resultado1.getNome()).isEqualTo("LEITURA");
        assertThat(resultado2.getNome()).isEqualTo("ESCRITA");
        assertThat(resultado1.getId()).isNotEqualTo(resultado2.getId());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException sequencialmente para IDs inválidos")
    void testBuscarPermissaoSequencialmenteInvalida() {
        UUID idInvalido1 = UUID.randomUUID();
        UUID idInvalido2 = UUID.randomUUID();

        when(permissaoRepository.findById(idInvalido1))
                .thenReturn(Optional.empty());
        when(permissaoRepository.findById(idInvalido2))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> buscarPermissaoService.buscarPermissao(idInvalido1));

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> buscarPermissaoService.buscarPermissao(idInvalido2));

        verify(permissaoRepository, times(2)).findById(org.mockito.ArgumentMatchers.any(UUID.class));
    }

    @Test
    @DisplayName("Deve verificar integridade dos dados da permissão retornada")
    void testBuscarPermissaoIntegridadeDados() {
        when(permissaoRepository.findById(permissaoId))
                .thenReturn(Optional.of(permissaoExistente));

        Permissao resultado = buscarPermissaoService.buscarPermissao(permissaoId);

        assertNotNull(resultado.getId());
        assertNotNull(resultado.getNome());
        assertThat(resultado.getId()).isEqualTo(permissaoId);
        assertThat(resultado.getNome()).isNotEmpty();
    }


    private Permissao criarPermissao() {
        Permissao permissao = new Permissao();
        permissao.setId(permissaoId);
        permissao.setNome("LEITURA");
        permissao.setDescricao("Permissão de leitura");
        return permissao;
    }

    private Permissao criarPermissaoComIdENome(UUID id, String nome) {
        Permissao permissao = new Permissao();
        permissao.setId(id);
        permissao.setNome(nome);
        permissao.setDescricao("Permissão de " + nome.toLowerCase());
        return permissao;
    }
}
