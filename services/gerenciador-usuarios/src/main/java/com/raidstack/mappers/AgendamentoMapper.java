package com.raidstack.mappers;

import com.raidstack.dtos.AtualizarAgendamentoDTO;
import com.raidstack.dtos.CadastrarAgendamentoDTO;
import com.raidstack.dtos.VisualizarAgendamentoDTO;
import com.raidstack.entities.Agendamento;
import com.raidstack.kafka.events.AgendamentoEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface AgendamentoMapper {

    AgendamentoMapper INSTANCE = Mappers.getMapper(AgendamentoMapper.class);

    VisualizarAgendamentoDTO agendamentoToVisualizarAgendamentoDTO(Agendamento agendamento);

    default Page<VisualizarAgendamentoDTO> agendamentoToVisualizarAgendamentoDTO(Page<Agendamento> agendamento) {
        return agendamento.map(this::agendamentoToVisualizarAgendamentoDTO);
    }

    @Mapping(target = "medico.id", source = "idMedico")
    @Mapping(target = "paciente.id", source = "idPaciente")
    Agendamento cadastrarAgendamentoDTOToAgendamento(CadastrarAgendamentoDTO cadastrarAgendamentoDTO);


    Agendamento atualizarAgendamentoDTOToAgendamento(AtualizarAgendamentoDTO atualizarAgendamentoDTO);

    @Mapping(target="idAgendamento",  source = "id")
    @Mapping(target="nomePaciente",  source = "paciente.nome")
    @Mapping(target="nomeMedico",  source = "medico.nome")
    AgendamentoEvent agentamentoToAgendamentoEvent(Agendamento agendamento);


}
