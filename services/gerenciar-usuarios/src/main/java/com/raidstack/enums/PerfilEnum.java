package com.raidstack.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PerfilEnum {

    ADMINISTRADOR("Administrador"),
    PACIENTE("Paciente"),
    MEDICO("Médico"),
    ENFERMEIRO("Enfermeiro"),
    ;

    private String descricao;

}
