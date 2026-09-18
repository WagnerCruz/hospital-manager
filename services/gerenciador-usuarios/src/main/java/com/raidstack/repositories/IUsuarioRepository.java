package com.raidstack.repositories;

import com.raidstack.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IUsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findUsuarioByEmail(String email);
    Optional<Usuario> findUsuarioByLogin(String login);
    Optional<Usuario> findUsuarioByNome(String nome);

    boolean existsByLogin(String login);
}
