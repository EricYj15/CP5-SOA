package br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.instrucao;

import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrucao;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Especialidade;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.MotivoCancelamento;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record DadosDetalhamentoAgendamento(
        Long id,

        @JsonProperty("nome_aluno")
        String nomeAluno,

        @JsonProperty("nome_instrutor")
        String nomeInstrutor,
        Especialidade especialidade,

        @JsonProperty("data_hora")
        @JsonFormat(pattern = "dd/MM/yyyy - HH:mm")
        LocalDateTime dataHora,

        @JsonProperty("motivo_cancelamento")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        MotivoCancelamento motivoCancelamento) {
    public DadosDetalhamentoAgendamento(Instrucao instrucao) {
        this(
                instrucao.getId(),
                instrucao.getAluno().getNome(),
                instrucao.getInstrutor().getNome(),
                instrucao.getInstrutor().getEspecialidade(),
                instrucao.getDataHora(),
                instrucao.getMotivoCancelamento()
        );
    }
}
