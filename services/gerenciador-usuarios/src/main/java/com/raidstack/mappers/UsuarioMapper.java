package com.raidstack.mappers;

import com.raidstack.dtos.AtualizarUsuarioDTO;
import com.raidstack.dtos.AtualizarUsuarioExternoDTO;
import com.raidstack.dtos.AtualizarUsuarioPerfilDTO;
import com.raidstack.dtos.CadastrarUsuarioDTO;
import com.raidstack.dtos.CadastrarUsuarioExternoDTO;
import com.raidstack.dtos.VisualizarUsuarioDTO;
import com.raidstack.entities.Perfil;
import com.raidstack.entities.Permissao;
import com.raidstack.entities.Usuario;
import com.raidstack.enums.PerfilEnum;
import com.raidstack.enums.PermissaoEnum;
import com.raidstack.kafka.events.UsuarioEvent;
import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    @Mapping(target = "perfis", expression = "java(converterListaPerfilToListaPerfilEnum(usuario.getPerfis()))")
    @Mapping(target = "permissoes", expression = "java(converterListaPermissoesToListaPermissaoEnum(usuario.getPerfis()))")
    VisualizarUsuarioDTO usuarioToVisualizarUsuarioDTO(Usuario usuario);

    default Page<VisualizarUsuarioDTO> usuarioToVisualizarUsuarioDTO(Page<Usuario> usuarios) {
        return usuarios.map(this::usuarioToVisualizarUsuarioDTO);
    }

    Usuario cadastrarUsuarioDTOToUsuario(CadastrarUsuarioDTO cadastrarUsuarioDTO);

    @Mapping(target = "perfis", expression = "java(converterListaPerfilEnumToListaPerfil(cadastrarUsuarioExternoDTO.perfis()))")
    Usuario cadastrarUsuarioExternoDTOToUsuario(CadastrarUsuarioExternoDTO cadastrarUsuarioExternoDTO);

    Usuario atualizarUsuarioDTOToUsuario(AtualizarUsuarioDTO atualizarUsuarioDTO);

    Usuario atualizarUsuarioExternoDTOToUsuario(AtualizarUsuarioExternoDTO atualizarUsuarioExternoDTO);

    @Mapping(target = "perfis", expression = "java(converterListaPerfilEnumToListaPerfil(atualizarUsuarioPerfilDTO.perfis()))")
    Usuario atualizarUsuarioPerfilDTOToUsuario(AtualizarUsuarioPerfilDTO atualizarUsuarioPerfilDTO);

    @Mapping(target = "perfis", expression = "java(converterListaPerfilToListaPerfilString(usuario.getPerfis()))")
    UsuarioEvent usuarioToUsuarioEvent(Usuario usuario);

    @Name("converterListaPerfilToListaPerfilEnum")
    default List<PerfilEnum> converterListaPerfilToListaPerfilEnum(List<Perfil> perfis) {
        return Optional.ofNullable(perfis).orElse(Collections.emptyList()).stream()
                .map(Perfil::getNome)
                .map(PerfilEnum::valueOf).toList();
    }

    @Name("converterListaPermissoesToListaPermissaoEnum")
    default List<PermissaoEnum> converterListaPermissoesToListaPermissaoEnum(List<Perfil> perfis) {
        return Optional.ofNullable(perfis).orElse(Collections.emptyList()).stream()
                .flatMap(perfil -> Optional.ofNullable(perfil.getPermissoes()).orElse(Collections.emptyList()).stream())
                .map(Permissao::getNome)
                .map(PermissaoEnum::valueOf).toList();
    }

    @Name("converterListaPerfilEnumToListaPerfil")
    default List<Perfil> converterListaPerfilEnumToListaPerfil(List<PerfilEnum> perfilEnums) {
        return perfilEnums.stream()
                .map(perfilEnum -> new Perfil(perfilEnum.name(), perfilEnum.getDescricao()))
                .toList();
    }

    @Name("converterListaPerfilToListaPerfilString")
    default List<String> converterListaPerfilToListaPerfilString(List<Perfil> perfis) {
        return Optional.ofNullable(perfis).orElse(Collections.emptyList()).stream()
                .map(Perfil::getNome)
                .toList();
    }

}
