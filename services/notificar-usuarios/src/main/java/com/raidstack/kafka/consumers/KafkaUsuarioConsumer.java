package com.raidstack.kafka.consumers;

import com.raidstack.kafka.events.UsuarioEvent;
import com.raidstack.services.INotificarUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class KafkaUsuarioConsumer {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private INotificarUsuario notificarUsuarioService;

    @KafkaListener(groupId = "${app.kafka.consumers.usuario.group-id}", topics = "${app.kafka.consumers.usuario.criado.topic}")
    public void usuarioCriadoAdmin(String mensagemJson) {
        System.out.println("Usuário criado pelo admin: " + mensagemJson);

        UsuarioEvent evento = mapper.readValue(mensagemJson, UsuarioEvent.class);
        this.notificarUsuarioService.notificarUsuarioCriado(evento);
    }

    @KafkaListener(groupId = "${app.kafka.consumers.usuario.group-id}", topics = "${app.kafka.consumers.usuario.atualizado.topic}")
    public void usuarioAtualizadoAdmin(String mensagemJson) {
        System.out.println("Usuário atualizado pelo admin: " + mensagemJson);

        UsuarioEvent evento = mapper.readValue(mensagemJson, UsuarioEvent.class);
        this.notificarUsuarioService.notificarUsuarioAtualizado(evento);
    }

}
