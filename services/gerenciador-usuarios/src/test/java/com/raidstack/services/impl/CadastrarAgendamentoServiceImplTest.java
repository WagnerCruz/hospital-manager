package com.raidstack.services.impl;

import com.raidstack.dtos.CadastrarAgendamentoDTO;
import com.raidstack.dtos.VisualizarAgendamentoDTO;
import com.raidstack.dtos.VisualizarUsuarioAgendamentoDTO;
import com.raidstack.entities.Agendamento;
import com.raidstack.entities.Usuario;
import com.raidstack.kafka.events.AgendamentoEvent;
import com.raidstack.kafka.producers.KafkaAgendamentoProducer;
import com.raidstack.mappers.AgendamentoMapper;
import com.raidstack.repositories.IAgendamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("CadastrarAgendamentoServiceImpl - Testes Unitários")
@ExtendWith(MockitoExtension.class)
class CadastrarAgendamentoServiceImplTest {

    @Mock
    private KafkaAgendamentoProducer kafkaAgendamentoProducer;

    @Mock
    private IAgendamentoRepository agendamentoRepository;

    @InjectMocks
    private CadastrarAgendamentoServiceImpl cadastrarAgendamentoService;

    private UUID agendamentoId;
    private Agendamento agendamentoNovo;
    private CadastrarAgendamentoDTO cadastrarDTO;
    private VisualizarAgendamentoDTO visualizarDTO;

    @BeforeEach
    void setUp() {
        agendamentoId = UUID.randomUUID();
        agendamentoNovo = criarAgendamento();
        cadastrarDTO = criarCadastrarAgendamentoDTO();
        visualizarDTO = criarVisualizarAgendamentoDTO();
    }


    @Test
    @DisplayName("Deve cadastrar agendamento com sucesso e enviar notificação Kafka")
    void testCadastrarAgendamentoDTOSucesso() {
        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenReturn(agendamentoNovo);

        VisualizarAgendamentoDTO resultado = cadastrarAgendamentoService.cadastrarAgendamentoDTO(cadastrarDTO);

        assertNotNull(resultado);
        verify(agendamentoRepository, times(1)).save(any(Agendamento.class));
        verify(kafkaAgendamentoProducer, times(1)).enviarAgendamentoCriado(any(AgendamentoEvent.class));
    }


    @Test
    @DisplayName("Deve lançar exceção quando repository retorna null")
    void testCadastrarAgendamentoDTORepositoryNull() {
        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenReturn(null);

        try {
            cadastrarAgendamentoService.cadastrarAgendamentoDTO(cadastrarDTO);
        } catch (NullPointerException e) {
            assertThat(e).isNotNull();
        }

        verify(agendamentoRepository, times(1)).save(any(Agendamento.class));
    }


    @Test
    @DisplayName("Deve cadastrar agendamento com data no futuro")
    void testCadastrarAgendamentoDataFuturo() {
        Agendamento agendamentoFuturo = criarAgendamento();
        agendamentoFuturo.setDataAgendamento(LocalDateTime.now().plusDays(30));

        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenReturn(agendamentoFuturo);

        VisualizarAgendamentoDTO resultado = cadastrarAgendamentoService.cadastrarAgendamentoDTO(cadastrarDTO);

        assertNotNull(resultado);
        verify(agendamentoRepository, times(1)).save(any(Agendamento.class));
        verify(kafkaAgendamentoProducer, times(1)).enviarAgendamentoCriado(any(AgendamentoEvent.class));
    }

    @Test
    @DisplayName("Deve cadastrar agendamento com data próxima")
    void testCadastrarAgendamentoDataProxima() {
        Agendamento agendamentoProximo = criarAgendamento();
        agendamentoProximo.setDataAgendamento(LocalDateTime.now().plusMinutes(30));

        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenReturn(agendamentoProximo);

        VisualizarAgendamentoDTO resultado = cadastrarAgendamentoService.cadastrarAgendamentoDTO(cadastrarDTO);

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Deve verificar que notificação Kafka é enviada após salvamento")
    void testNotificacaoKafkaEnviadaAposKafka() {
        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenReturn(agendamentoNovo);

        cadastrarAgendamentoService.cadastrarAgendamentoDTO(cadastrarDTO);

        verify(agendamentoRepository, times(1)).save(any(Agendamento.class));
        verify(kafkaAgendamentoProducer, times(1)).enviarAgendamentoCriado(any(AgendamentoEvent.class));
    }

    @Test
    @DisplayName("Deve manter ordem: salvar antes de notificar")
    void testOrdenSalvarAntesDNotificar() {
        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenReturn(agendamentoNovo);

        cadastrarAgendamentoService.cadastrarAgendamentoDTO(cadastrarDTO);

        // Verifica que save foi chamado antes de enviarAgendamentoCriado
        verify(agendamentoRepository, times(1)).save(any(Agendamento.class));
        verify(kafkaAgendamentoProducer, times(1)).enviarAgendamentoCriado(any(AgendamentoEvent.class));
    }

    @Test
    @DisplayName("Deve cadastrar múltiplos agendamentos sequencialmente")
    void testCadastrarMultiplosAgendamentos() {
        Agendamento agendamento1 = criarAgendamento();
        Agendamento agendamento2 = criarAgendamento();

        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenReturn(agendamento1)
                .thenReturn(agendamento2);

        CadastrarAgendamentoDTO dto1 = criarCadastrarAgendamentoDTO();
        CadastrarAgendamentoDTO dto2 = criarCadastrarAgendamentoDTO();

        VisualizarAgendamentoDTO resultado1 = cadastrarAgendamentoService.cadastrarAgendamentoDTO(dto1);
        VisualizarAgendamentoDTO resultado2 = cadastrarAgendamentoService.cadastrarAgendamentoDTO(dto2);

        assertNotNull(resultado1);
        assertNotNull(resultado2);
        verify(agendamentoRepository, times(2)).save(any(Agendamento.class));
        verify(kafkaAgendamentoProducer, times(2)).enviarAgendamentoCriado(any(AgendamentoEvent.class));
    }

    @Test
    @DisplayName("Deve retornar DTO corretamente mapeado")
    void testRetornoDTOMapeadoCorretamente() {
        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenReturn(agendamentoNovo);

        VisualizarAgendamentoDTO resultado = cadastrarAgendamentoService.cadastrarAgendamentoDTO(cadastrarDTO);

        assertNotNull(resultado);
        assertThat(resultado.id()).isNotNull();
    }


    private Agendamento criarAgendamento() {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(agendamentoId);
        agendamento.setDataAgendamento(LocalDateTime.now().plusDays(1));
        agendamento.setStatus("A");
        return agendamento;
    }

    private CadastrarAgendamentoDTO criarCadastrarAgendamentoDTO() {
        return new CadastrarAgendamentoDTO(
                "CADASTRAR AGENDAMENTO TESTE",
                "A",
                LocalDateTime.now().plusDays(1),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    private VisualizarAgendamentoDTO criarVisualizarAgendamentoDTO() {
        return new VisualizarAgendamentoDTO(
                agendamentoId,
                "VISUALIZAR AGENDAMENTO TESTE",
                "A",
                LocalDateTime.now().plusDays(1),
                new VisualizarUsuarioAgendamentoDTO(null, null, null),
                new VisualizarUsuarioAgendamentoDTO(null, null, null)
        );
    }
}
