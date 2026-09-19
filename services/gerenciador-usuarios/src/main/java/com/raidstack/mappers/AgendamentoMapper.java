package com.raidstack.mappers;

import com.raidstack.dtos.AtualizarAgendamentoDTO;
import com.raidstack.dtos.CadastrarAgendamentoDTO;
import com.raidstack.dtos.VisualizarAgendamentoDTO;
import com.raidstack.entities.Agendamento;
import com.raidstack.entities.Usuario;
import com.raidstack.kafka.events.AgendamentoEvent;
import com.raidstack.kafka.events.UsuarioEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;

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


    AgendamentoEvent agendamentoToAgendamentoEvent(Agendamento agendamento);
}
