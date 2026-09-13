package com.raidstack.repositories;

import com.raidstack.entities.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IPermissaoRepository extends JpaRepository<Permissao, UUID> {
}
