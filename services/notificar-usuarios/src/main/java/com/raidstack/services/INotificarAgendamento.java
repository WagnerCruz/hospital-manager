package com.raidstack.services;

import com.raidstack.kafka.events.AgendamentoEvent;
import com.raidstack.kafka.events.UsuarioEvent;

public interface INotificarAgendamento {
    void notificarAgendamentoCriado(AgendamentoEvent agendamentoEvent);
    void notificarAgendamentoAtualizado(AgendamentoEvent agendamentoEvent);
}
