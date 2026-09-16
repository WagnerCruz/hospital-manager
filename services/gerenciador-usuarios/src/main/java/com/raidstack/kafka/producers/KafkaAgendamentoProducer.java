package com.raidstack.kafka.producers;

import com.raidstack.kafka.events.AgendamentoEvent;
import com.raidstack.kafka.events.UsuarioEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaAgendamentoProducer {

    @Value("${app.kafka.producers.agendamento.criado.topic}")
    private String agendamentoCriadoTopico;

    @Value("${app.kafka.producers.agendamento.atualizado.topic}")
    private String agendamentoAtualizadoTopico;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void enviarAgendamentoCriado(AgendamentoEvent agendamentoEvent) {
        kafkaTemplate.send(agendamentoCriadoTopico, agendamentoEvent);
    }

    public void enviarAgendamentoAtualizado(AgendamentoEvent agendamentoEvent) {
        kafkaTemplate.send(agendamentoAtualizadoTopico, agendamentoEvent);
    }

}
