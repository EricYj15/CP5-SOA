package br.com.fiap3ess.autoescola3ess.adapter.in.controller;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosCancelamento;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.instrucao.DadosDetalhamentoAgendamento;
import br.com.fiap3ess.autoescola3ess.application.service.AgendaDeInstrucoes;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Especialidade;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.MotivoCancelamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InstrucaoControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AgendaDeInstrucoes agenda;

    private final LocalDateTime proximaSegundaAs10 = LocalDate.now()
            .plusWeeks(1)
            .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
            .atTime(10, 0);

    private final String dataHoraFormatada = proximaSegundaAs10.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"));

    @Test
    @DisplayName("Expectativa: Retornar código 400 para dados inválidos.")
    @WithMockUser
    void agendarInstrucaoCenario1() throws Exception {
        //act
        MockHttpServletResponse response = mockMvc
                .perform(post("/instrucoes"))
                .andReturn()
                .getResponse();

        //assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 200 e os dados do agendamento para dados válidos.")
    @WithMockUser
    void agendarInstrucaoCenario2() throws Exception {
        //arrange
        var detalhamento = new DadosDetalhamentoAgendamento(
                1L, "Aluno Teste", "Instrutor Teste", Especialidade.CARROS, proximaSegundaAs10, null);
        when(agenda.agendar(any(DadosAgendamento.class))).thenReturn(detalhamento);

        //act
        MockHttpServletResponse response = mockMvc
                .perform(post("/instrucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id_aluno": 1, "especialidade": "CARROS", "data_hora": "%s"}
                                """.formatted(dataHoraFormatada)))
                .andReturn()
                .getResponse();

        //assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString())
                .contains("\"nome_aluno\":\"Aluno Teste\"")
                .contains("\"data_hora\":\"" + dataHoraFormatada + "\"")
                .doesNotContain("motivo_cancelamento");

        ArgumentCaptor<DadosAgendamento> captor = ArgumentCaptor.forClass(DadosAgendamento.class);
        verify(agenda).agendar(captor.capture());
        assertThat(captor.getValue().idAluno()).isEqualTo(1L);
        assertThat(captor.getValue().dataHora()).isEqualTo(proximaSegundaAs10);
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 com a mensagem da regra de negócio violada.")
    @WithMockUser
    void agendarInstrucaoCenario3() throws Exception {
        //arrange
        when(agenda.agendar(any())).thenThrow(new ValidacaoException("Aluno já possui instrução agendada para este dia!"));

        //act + assert
        mockMvc.perform(post("/instrucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id_aluno": 1, "id_instrutor": 1, "data_hora": "%s"}
                                """.formatted(dataHoraFormatada)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Aluno já possui instrução agendada para este dia!"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 quando a data/hora estiver no passado.")
    @WithMockUser
    void agendarInstrucaoCenario4() throws Exception {
        mockMvc.perform(post("/instrucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id_aluno": 1, "especialidade": "CARROS", "data_hora": "01/01/2020 - 10:00"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].campo").value("dataHora"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 200 com a lista de instruções agendadas.")
    @WithMockUser
    void listarInstrucoesCenario1() throws Exception {
        //arrange
        var detalhamento = new DadosDetalhamentoAgendamento(
                1L, "Aluno Teste", "Instrutor Teste", Especialidade.CARROS, proximaSegundaAs10, null);
        when(agenda.listar(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(detalhamento)));

        //act + assert
        mockMvc.perform(get("/instrucoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome_instrutor").value("Instrutor Teste"));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 204 ao cancelar instrução informando o motivo.")
    @WithMockUser
    void cancelarInstrucaoCenario1() throws Exception {
        mockMvc.perform(delete("/instrucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id_instrucao": 1, "motivo": "ALUNO_DESISTIU"}
                                """))
                .andExpect(status().isNoContent());

        verify(agenda).cancelar(new DadosCancelamento(1L, MotivoCancelamento.ALUNO_DESISTIU));
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 ao cancelar sem informar o motivo.")
    @WithMockUser
    void cancelarInstrucaoCenario2() throws Exception {
        mockMvc.perform(delete("/instrucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id_instrucao": 1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].campo").value("motivo"));
    }
}
