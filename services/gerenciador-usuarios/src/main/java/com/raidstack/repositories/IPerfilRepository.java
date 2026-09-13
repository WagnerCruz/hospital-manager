package com.raidstack.repositories;

import com.raidstack.entities.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IPerfilRepository extends JpaRepository<Perfil, UUID> {

    Optional<Perfil> findByNome(String nome);

}
