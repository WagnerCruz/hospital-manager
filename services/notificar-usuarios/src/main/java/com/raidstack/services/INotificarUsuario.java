package com.raidstack.services;

import com.raidstack.kafka.events.UsuarioEvent;

public interface INotificarUsuario {
    void notificarUsuarioCriado(UsuarioEvent usuarioEvent);
    void notificarUsuarioAtualizado(UsuarioEvent usuarioEvent);
}
