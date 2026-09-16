package com.raidstack.services.impl;

import com.raidstack.enums.TipoNotificacaoEnum;
import com.raidstack.kafka.events.UsuarioEvent;
import com.raidstack.services.INotificarAgendamento;
import com.raidstack.services.INotificarUsuario;
import org.springframework.stereotype.Service;

@Service
public class NotificarUsuarioServiceImpl implements INotificarUsuario {

    public void notificarUsuarioCriado(UsuarioEvent usuarioEvent) {
        String mensagem = this.mensagemUsuario(usuarioEvent, TipoNotificacaoEnum.USUARIO_CADASTRAR.getApresentacao());
        System.out.println(mensagem);
    }

    public void notificarUsuarioAtualizado(UsuarioEvent usuarioEvent) {
        String mensagem = this.mensagemUsuario(usuarioEvent, TipoNotificacaoEnum.USUARIO_ATUALIZAR.getApresentacao());
        System.out.println(mensagem);
    }

    private String mensagemUsuario(UsuarioEvent usuarioEvent, String operacao) {
        String mensagem = "Olá " + usuarioEvent.getNome() + ",\n\n";
        mensagem += "Seu usuário(a) foi " + operacao + " com sucesso.\n\n";
        mensagem += "Login: " + usuarioEvent.getLogin() + "\n";
        mensagem += "Senha: " + usuarioEvent.getSenha() + "\n";
        mensagem += "OBSERVAÇÃO: Senha criada temporáriamente, necessário trocar no primeiro login\n";
        mensagem += "Atenciosamente,\n";
        mensagem += "Equipe Hospital Manager.";
        return mensagem;
    }

}
