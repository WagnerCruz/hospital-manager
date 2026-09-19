package com.raidstack.repositories;

import com.raidstack.entities.Agendamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IAgendamentoRepository extends JpaRepository<Agendamento, UUID> {

    Page<Agendamento> findById(UUID id, Pageable pageable);

}
