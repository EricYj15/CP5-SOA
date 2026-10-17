package br.com.fiap3ess.autoescola3ess.application.core.specification;

import br.com.fiap3ess.autoescola3ess.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

@Component
public class ValidadorHorarioFuncionamento implements ValidadorAgendamento {
    @Override
    public void validar(DadosAgendamento dados) {
        boolean domingo = dados.dataHora().getDayOfWeek().equals(DayOfWeek.SUNDAY);
        boolean preAbertura = dados.dataHora().getHour() < 6;
        boolean posFechamento = dados.dataHora().getHour() > 21 - 1;

        if (domingo || preAbertura || posFechamento) {
            throw new ValidacaoException("Tentativa de agendamento fora do horário de funcionamento!");
        }
    }
}