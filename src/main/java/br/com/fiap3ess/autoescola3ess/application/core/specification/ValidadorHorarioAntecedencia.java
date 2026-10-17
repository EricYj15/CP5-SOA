package br.com.fiap3ess.autoescola3ess.application.core.specification;

import br.com.fiap3ess.autoescola3ess.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ValidadorHorarioAntecedencia implements ValidadorAgendamento {
    @Override
    public void validar(DadosAgendamento dados) {
        LocalDateTime dataEscolhida = dados.dataHora();
        LocalDateTime agora = LocalDateTime.now();

        long antecedencia = Duration.between(agora, dataEscolhida).toMinutes();

        if (antecedencia < 30) {
            throw new ValidacaoException("Agendamento deve respeitar antecedencia mínima de 30 minutos!");
        }
    }
}