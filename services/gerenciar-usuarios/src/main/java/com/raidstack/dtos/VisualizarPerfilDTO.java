package com.raidstack.dtos;

import java.util.List;

public record VisualizarPerfilDTO(
        String nome,
        String descricao,
        List<VisualizarPermissoesDTO> permissoes
) {
}
