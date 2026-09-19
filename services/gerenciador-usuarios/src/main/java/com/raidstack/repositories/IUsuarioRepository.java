package com.raidstack.repositories;

import com.raidstack.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IUsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByLogin(String login);
    Optional<Usuario> findByNome(String nome);

    boolean existsByLogin(String login);
}
