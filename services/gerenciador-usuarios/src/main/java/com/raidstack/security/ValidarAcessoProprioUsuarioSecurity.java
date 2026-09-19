package com.raidstack.security;

import com.raidstack.entities.Usuario;
import com.raidstack.enums.PerfilEnum;
import com.raidstack.enums.PermissaoEnum;
import com.raidstack.repositories.IUsuarioRepository;
import com.raidstack.services.exceptions.ResourceConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ValidarAcessoProprioUsuarioSecurity {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    public boolean visualizar(UUID id, Authentication authentication) {

        boolean admin = this.getPermissoes()
                .stream()
                .anyMatch(auth ->
                        PermissaoEnum.ADMINISTRADOR_USUARIO_VISUALIZAR.name().equals(auth.getAuthority())
                );

        if (admin) {
            return true;
        }

        return validar(id, authentication);
    }

    public boolean validar(UUID id, Authentication authentication) {
        return usuarioRepository.findById(id)
                .map(usuario ->
                        usuario.getLogin().equals(
                                authentication.getName()
                        )
                )
                .orElse(false);
    }

    private List<GrantedAuthority> getPermissoes() {
        return new ArrayList<>(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    }

    public String obterLoginUsuarioLogado() {
        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();
        return authentication.getName();
    }

    public Usuario obterUsuarioLogado() {
        return this.usuarioRepository.findByLogin(this.obterLoginUsuarioLogado())
                .orElseThrow(() -> new ResourceConflictException("Não foi possível encontrar o usuário logado, atualize o acesso"));
    }



}
