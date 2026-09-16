package com.raidstack.services.impl;

import com.raidstack.entities.Perfil;
import com.raidstack.repositories.IPerfilRepository;
import com.raidstack.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("BuscarPerfilServiceImpl - Testes Unitários")
@ExtendWith(MockitoExtension.class)
class BuscarPerfilServiceImplTest {

    @Mock
    private IPerfilRepository perfilRepository;

    @InjectMocks
    private BuscarPerfilServiceImpl buscarPerfilService;

    private UUID perfilId;
    private Perfil perfilExistente;
    private static final String NOME_PERFIL = "PACIENTE";

    @BeforeEach
    void setUp() {
        perfilId = UUID.randomUUID();
        perfilExistente = criarPerfil();
    }

    @Test
    @DisplayName("Deve encontrar perfil por ID com sucesso")
    void testBuscarPerfilPorIdSucesso() {
        when(perfilRepository.findById(perfilId))
                .thenReturn(Optional.of(perfilExistente));

        Perfil resultado = buscarPerfilService.buscarPerfilPorId(perfilId);

        assertNotNull(resultado);
        assertThat(resultado.getId()).isEqualTo(perfilId);
        assertThat(resultado.getNome()).isEqualTo(NOME_PERFIL);
        verify(perfilRepository, times(1)).findById(perfilId);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando perfil não é encontrado por ID")
    void testBuscarPerfilPorIdNaoEncontrado() {
        UUID idInexistente = UUID.randomUUID();
        when(perfilRepository.findById(idInexistente))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> buscarPerfilService.buscarPerfilPorId(idInexistente))
                .withMessageContaining("Perfil não encontrado")
                .withMessageContaining("ID")
                .withMessageContaining(idInexistente.toString());

        verify(perfilRepository, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void testBuscarPerfilPorIdNulo() {
        when(perfilRepository.findById(null))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> buscarPerfilService.buscarPerfilPorId(null));
    }


    @Test
    @DisplayName("Deve encontrar perfil por nome com sucesso")
    void testBuscarPerfilPorNomeSucesso() {
        when(perfilRepository.findByNome(NOME_PERFIL))
                .thenReturn(Optional.of(perfilExistente));

        Perfil resultado = buscarPerfilService.buscarPerfilPorNome(NOME_PERFIL);

        assertNotNull(resultado);
        assertThat(resultado.getNome()).isEqualTo(NOME_PERFIL);
        assertThat(resultado.getId()).isEqualTo(perfilId);
        verify(perfilRepository, times(1)).findByNome(NOME_PERFIL);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando perfil não é encontrado por nome")
    void testBuscarPerfilPorNomeNaoEncontrado() {
        String nomeInexistente = "PERFIL_INEXISTENTE";
        when(perfilRepository.findByNome(nomeInexistente))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> buscarPerfilService.buscarPerfilPorNome(nomeInexistente))
                .withMessageContaining("Perfil não encontrado")
                .withMessageContaining("NOME")
                .withMessageContaining(nomeInexistente);

        verify(perfilRepository, times(1)).findByNome(nomeInexistente);
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é nulo")
    void testBuscarPerfilPorNomeNulo() {
        when(perfilRepository.findByNome(null))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> buscarPerfilService.buscarPerfilPorNome(null));
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é string vazia")
    void testBuscarPerfilPorNomeVazio() {
        String nomeVazio = "";
        when(perfilRepository.findByNome(nomeVazio))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> buscarPerfilService.buscarPerfilPorNome(nomeVazio));
    }


    @Test
    @DisplayName("Deve encontrar perfil com nome em maiúsculas")
    void testBuscarPerfilPorNomeMaiusculas() {
        String nomeMaiusculo = "MEDICO";
        when(perfilRepository.findByNome(nomeMaiusculo))
                .thenReturn(Optional.of(criarPerfilComNome(nomeMaiusculo)));

        Perfil resultado = buscarPerfilService.buscarPerfilPorNome(nomeMaiusculo);

        assertNotNull(resultado);
        assertThat(resultado.getNome()).isEqualTo(nomeMaiusculo);
    }

    @Test
    @DisplayName("Deve encontrar múltiplos perfis por ID diferentes")
    void testBuscarMultiplosPerfisComIdsdiferentes() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Perfil perfil1 = criarPerfilComId(id1, "PERFIL_1");
        Perfil perfil2 = criarPerfilComId(id2, "PERFIL_2");

        when(perfilRepository.findById(id1))
                .thenReturn(Optional.of(perfil1));
        when(perfilRepository.findById(id2))
                .thenReturn(Optional.of(perfil2));

        Perfil resultado1 = buscarPerfilService.buscarPerfilPorId(id1);
        Perfil resultado2 = buscarPerfilService.buscarPerfilPorId(id2);

        assertThat(resultado1.getId()).isEqualTo(id1);
        assertThat(resultado2.getId()).isEqualTo(id2);
        assertThat(resultado1.getNome()).isNotEqualTo(resultado2.getNome());
    }


    private Perfil criarPerfil() {
        Perfil perfil = new Perfil();
        perfil.setId(perfilId);
        perfil.setNome(NOME_PERFIL);
        perfil.setDescricao("Descrição do Paciente");
        perfil.setPermissoes(new ArrayList<>());
        return perfil;
    }

    private Perfil criarPerfilComNome(String nome) {
        Perfil perfil = new Perfil();
        perfil.setId(UUID.randomUUID());
        perfil.setNome(nome);
        perfil.setDescricao("Descrição do " + nome);
        perfil.setPermissoes(new ArrayList<>());
        return perfil;
    }

    private Perfil criarPerfilComId(UUID id, String nome) {
        Perfil perfil = new Perfil();
        perfil.setId(id);
        perfil.setNome(nome);
        perfil.setDescricao("Descrição de " + nome);
        perfil.setPermissoes(new ArrayList<>());
        return perfil;
    }
}
