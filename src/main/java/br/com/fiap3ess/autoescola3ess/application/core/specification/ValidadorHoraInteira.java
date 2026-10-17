package br.com.fiap3ess.autoescola3ess.application.core.specification;

import br.com.fiap3ess.autoescola3ess.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ValidadorHoraInteira implements ValidadorAgendamento {
    @Override
    public void validar(DadosAgendamento dados) {
        LocalDateTime dataEscolhida = dados.dataHora();

        if (dataEscolhida.getMinute() != 0) {
            throw new ValidacaoException("O campo precisa ser preenchido com horas inteiras (ex: 09:00, 13:00, ...)");
        }
    }
}