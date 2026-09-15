package com.raidstack.mappers;

import com.raidstack.dtos.AtualizarAgendamentoDTO;
import com.raidstack.dtos.CadastrarAgendamentoDTO;
import com.raidstack.dtos.VisualizarAgendamentoDTO;
import com.raidstack.entities.Agendamento;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface AgendamentoMapper {

    AgendamentoMapper INSTANCE = Mappers.getMapper(AgendamentoMapper.class);

    VisualizarAgendamentoDTO agendamentoToVisualizarAgendamentoDTO(Agendamento agendamento);

    default Page<VisualizarAgendamentoDTO> agendamentoToVisualizarAgendamentoDTO(Page<Agendamento> agendamento) {
        return agendamento.map(this::agendamentoToVisualizarAgendamentoDTO);
    }

    Agendamento cadastrarAgendamentoDTOToAgendamento(CadastrarAgendamentoDTO cadastrarAgendamentoDTO);


    Agendamento atualizarAgendamentoDTOToAgendamento(AtualizarAgendamentoDTO atualizarAgendamentoDTO);


}
