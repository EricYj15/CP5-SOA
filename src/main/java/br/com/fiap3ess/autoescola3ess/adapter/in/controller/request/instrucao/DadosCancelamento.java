package br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao;

import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.MotivoCancelamento;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record DadosCancelamento(
        @NotNull
        @JsonProperty("id_instrucao")
        Long idInstrucao,

        @NotNull
        MotivoCancelamento motivo) {
}
