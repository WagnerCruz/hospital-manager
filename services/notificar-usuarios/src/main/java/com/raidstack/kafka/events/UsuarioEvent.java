package com.raidstack.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEvent {

    private String login;

    private String email;

    private String nome;

    private String senha;

    private List<String> perfis = new ArrayList<>();

}
