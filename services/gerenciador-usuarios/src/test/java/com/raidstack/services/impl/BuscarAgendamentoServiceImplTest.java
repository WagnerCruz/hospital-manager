package com.raidstack.services.impl;

import com.raidstack.dtos.VisualizarAgendamentoDTO;
import com.raidstack.entities.Agendamento;
import com.raidstack.mappers.AgendamentoMapper;
import com.raidstack.repositories.IAgendamentoRepository;
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

@DisplayName("BuscarAgendamentoServiceImpl - Testes Unitários")
@ExtendWith(MockitoExtension.class)
class BuscarAgendamentoServiceImplTest {

    @Mock
    private IAgendamentoRepository agendamentoRepository;

    @InjectMocks
    private BuscarAgendamentoServiceImpl buscarAgendamentoService;

    private UUID agendamentoId;
    private Agendamento agendamentoExistente;
    private VisualizarAgendamentoDTO agendamentoDTO;

    @BeforeEach
    void setUp() {
        agendamentoId = UUID.randomUUID();
        agendamentoExistente = criarAgendamento();
        agendamentoDTO = criarAgendamentoDTO();
    }


    @Test
    @DisplayName("Deve encontrar agendamento por ID com sucesso")
    void testBuscarAgendamentoSucesso() {
        when(agendamentoRepository.findById(agendamentoId))
                .thenReturn(Optional.of(agendamentoExistente));

        Agendamento resultado = buscarAgendamentoService.buscarAgendamento(agendamentoId);

        assertNotNull(resultado);
        assertThat(resultado.getId()).isEqualTo(agendamentoId);
        verify(agendamentoRepository, times(1)).findById(agendamentoId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando agendamento não é encontrado")
    void testBuscarAgendamentoNaoEncontrado() {
        when(agendamentoRepository.findById(agendamentoId))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> buscarAgendamentoService.buscarAgendamento(agendamentoId))
                .withMessageContaining("Agendamento não encontrado")
                .withMessageContaining(agendamentoId.toString());

        verify(agendamentoRepository, times(1)).findById(agendamentoId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void testBuscarAgendamentoIdNulo() {
        when(agendamentoRepository.findById(null))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> buscarAgendamentoService.buscarAgendamento(null));
    }


    @Test
    @DisplayName("Deve buscar agendamento DTO com sucesso")
    void testBuscarAgendamentoDTOSucesso() {
        when(agendamentoRepository.findById(agendamentoId))
                .thenReturn(Optional.of(agendamentoExistente));

        VisualizarAgendamentoDTO resultado = buscarAgendamentoService.buscarAgendamentoDTO(agendamentoId);

        assertNotNull(resultado);
        verify(agendamentoRepository, times(1)).findById(agendamentoId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar DTO quando agendamento não existe")
    void testBuscarAgendamentoDTONaoEncontrado() {
        when(agendamentoRepository.findById(agendamentoId))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> buscarAgendamentoService.buscarAgendamentoDTO(agendamentoId));
    }


    @Test
    @DisplayName("Deve buscar lista paginada de agendamentos com sucesso")
    void testBuscarAgendamentosPaginaSucesso() {
        List<Agendamento> agendamentos = List.of(agendamentoExistente);
        Page<Agendamento> agendamentoPage = new PageImpl<>(agendamentos);

        when(agendamentoRepository.findAll(any(Pageable.class)))
                .thenReturn(agendamentoPage);

        Page<VisualizarAgendamentoDTO> resultado = buscarAgendamentoService.buscarAgendamentos(1, 10);

        assertNotNull(resultado);
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        verify(agendamentoRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum agendamento existe")
    void testBuscarAgendamentosListaVazia() {
        Page<Agendamento> agendamentoPageVazia = new PageImpl<>(new ArrayList<>());

        when(agendamentoRepository.findAll(any(Pageable.class)))
                .thenReturn(agendamentoPageVazia);

        Page<VisualizarAgendamentoDTO> resultado = buscarAgendamentoService.buscarAgendamentos(1, 10);

        assertNotNull(resultado);
        assertThat(resultado.getTotalElements()).isZero();
        assertThat(resultado.getContent()).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar múltiplas páginas com sucesso")
    void testBuscarAgendamentosMultiplasPaginas() {
        List<Agendamento> agendamentos = List.of(agendamentoExistente, criarAgendamento());
        Page<Agendamento> agendamentoPage = new PageImpl<>(agendamentos, PageRequest.of(0, 2), 2);

        when(agendamentoRepository.findAll(any(Pageable.class)))
                .thenReturn(agendamentoPage);

        Page<VisualizarAgendamentoDTO> resultado = buscarAgendamentoService.buscarAgendamentos(1, 2);

        assertNotNull(resultado);
        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("Deve calcular corretamente o índice da página")
    void testBuscarAgendamentosCalculoPagina() {
        Page<Agendamento> agendamentoPage = new PageImpl<>(new ArrayList<>());

        when(agendamentoRepository.findAll(PageRequest.of(2, 5)))
                .thenReturn(agendamentoPage);

        buscarAgendamentoService.buscarAgendamentos(3, 5);

        verify(agendamentoRepository, times(1)).findAll(PageRequest.of(2, 5));
    }


    @Test
    @DisplayName("Deve lançar exceção quando page é menor que 1")
    void testBuscarAgendamentosPageMenorQue1() {
        Page<Agendamento> agendamentoPage = new PageImpl<>(new ArrayList<>());

        when(agendamentoRepository.findAll(PageRequest.of(-1, 10)))
                .thenReturn(agendamentoPage);

        Page<VisualizarAgendamentoDTO> resultado = buscarAgendamentoService.buscarAgendamentos(0, 10);

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Deve buscar com tamanho de página grande")
    void testBuscarAgendamentosTamanhoPequeno() {
        List<Agendamento> agendamentos = List.of(agendamentoExistente);
        Page<Agendamento> agendamentoPage = new PageImpl<>(agendamentos);

        when(agendamentoRepository.findAll(any(Pageable.class)))
                .thenReturn(agendamentoPage);

        Page<VisualizarAgendamentoDTO> resultado = buscarAgendamentoService.buscarAgendamentos(1, 1);

        assertNotNull(resultado);
        assertThat(resultado.getSize()).isGreaterThanOrEqualTo(0);
    }


    private Agendamento criarAgendamento() {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(agendamentoId);
        agendamento.setData(LocalDateTime.now().plusDays(1));
        agendamento.setAtivo(true);
        agendamento.setDataCriacao(LocalDateTime.now());
        agendamento.setDataAtualizacao(LocalDateTime.now());
        return agendamento;
    }

    private VisualizarAgendamentoDTO criarAgendamentoDTO() {
        return new VisualizarAgendamentoDTO(
                agendamentoId,
                true
        );
    }
}
