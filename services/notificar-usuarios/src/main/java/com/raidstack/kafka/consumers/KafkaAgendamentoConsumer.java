package com.raidstack.kafka.consumers;

import com.raidstack.kafka.events.AgendamentoEvent;
import com.raidstack.services.INotificarAgendamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class KafkaAgendamentoConsumer {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private INotificarAgendamento notificarAgendamento;

    @KafkaListener(
            groupId = "${app.kafka.consumers.agendamento.group-id}",
            topics = "${app.kafka.consumers.agendamento.criado.topic}"
    )
    public void agendamentoCriado(String mensagemJson) {
        System.out.println("Agendamento criado: " + mensagemJson);
        AgendamentoEvent agendamento = mapper.readValue(mensagemJson, AgendamentoEvent.class);
        this.notificarAgendamento.notificarAgendamentoCriado(agendamento);
    }

    @KafkaListener(
            groupId = "${app.kafka.consumers.agendamento.group-id}",
            topics = "${app.kafka.consumers.agendamento.atualizado.topic}"
    )
    public void agendamentoAtualizado(String mensagemJson) {
        System.out.println("Agendamento atualizado: " + mensagemJson);
        AgendamentoEvent agendamento = mapper.readValue(mensagemJson, AgendamentoEvent.class);
        this.notificarAgendamento.notificarAgendamentoAtualizado(agendamento);
    }

}
