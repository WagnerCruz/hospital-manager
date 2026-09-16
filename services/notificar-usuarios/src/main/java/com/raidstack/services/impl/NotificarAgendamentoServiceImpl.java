package com.raidstack.services.impl;

import com.raidstack.enums.TipoNotificacaoEnum;
import com.raidstack.kafka.events.AgendamentoEvent;
import com.raidstack.kafka.events.UsuarioEvent;
import com.raidstack.services.INotificarAgendamento;
import org.springframework.stereotype.Service;

@Service
public class NotificarAgendamentoServiceImpl implements INotificarAgendamento {

    public void notificarAgendamentoCriado(AgendamentoEvent agendamentoEvent) {
        String mensagem = this.mensagemAgendamento(agendamentoEvent, TipoNotificacaoEnum.AGENDAMENTO_CADASTRAR.getApresentacao());
        System.out.println(mensagem);
    }

    public void notificarAgendamentoAtualizado(AgendamentoEvent agendamentoEvent) {
        String mensagem = this.mensagemAgendamento(agendamentoEvent, TipoNotificacaoEnum.AGENDAMENTO_ATUALIZAR.getApresentacao());
        System.out.println(mensagem);
    }

    private String mensagemAgendamento(AgendamentoEvent agendamentoEvent, String operacao) {
        String mensagem = "Olá " + agendamentoEvent.getNomePaciente() + ",\n\n";
        mensagem += "Seu agendamento foi " + operacao + " com sucesso.\n\n";
        mensagem += "Data: " + agendamentoEvent.getDataAgendamentoMostrar() + "\n";
        mensagem += "Horário: " + agendamentoEvent.getHorarioAgendamentoMostrar() + "\n";
        mensagem += "Médico: " + agendamentoEvent.getNomeMedico() + "\n";
        mensagem += "Descrição da Consulta: " + agendamentoEvent.getDescricao() + "\n";
        mensagem += "Atenciosamente,\n";
        mensagem += "Equipe Hospital Manager.";
        return mensagem;
    }

}
