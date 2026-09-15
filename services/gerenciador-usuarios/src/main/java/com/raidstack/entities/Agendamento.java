package com.raidstack.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "agendamento", schema = "hospital")
@NoArgsConstructor
@AllArgsConstructor
public class Agendamento {

    public Agendamento(String descricao, LocalDateTime dataAgendamento) {
        this.descricao = descricao;
        this.dataAgendamento = dataAgendamento;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String descricao;

    @Column(nullable = false)
    private String status = "A";

    @Column(nullable = false)
    private LocalDateTime dataAgendamento = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    private Usuario medico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Usuario paciente;

}
