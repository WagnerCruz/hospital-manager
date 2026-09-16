package com.raidstack.kafka.producers;

import com.raidstack.kafka.events.UsuarioEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaUsuarioProducer {

    @Value("${app.kafka.producers.usuario.criado.topic}")
    private String usuarioCriadoTopico;

    @Value("${app.kafka.producers.usuario.atualizado.topic}")
    private String usuarioAtualizadoTopico;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void enviarUsuarioCriado(UsuarioEvent usuarioEvent) {
        kafkaTemplate.send(usuarioCriadoTopico, usuarioEvent);
    }

    public void enviarUsuarioAtualizado(UsuarioEvent usuarioEvent) {
        kafkaTemplate.send(usuarioAtualizadoTopico, usuarioEvent);
    }

}
