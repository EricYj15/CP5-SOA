package br.com.fiap3ess.autoescola3ess.application.core.specification;

import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrucao;
import br.com.fiap3ess.autoescola3ess.application.core.specification.contract.ValidadorCancelamento;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ValidadorCancelamentoAntecedencia implements ValidadorCancelamento {
    @Override
    public void validar(Instrucao instrucao) {
        LocalDateTime agora = LocalDateTime.now();

        long antecedencia = Duration.between(agora, instrucao.getDataHora()).toHours();

        if (antecedencia < 24) {
            throw new ValidacaoException("Cancelamento deve ser feito com antecedência mínima de 24 horas!");
        }
    }
}
