package br.com.fiap3ess.autoescola3ess.application.core.specification;

import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrucao;
import br.com.fiap3ess.autoescola3ess.application.core.specification.contract.ValidadorCancelamento;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import org.springframework.stereotype.Component;

@Component
public class ValidadorCancelamentoInstrucaoAtiva implements ValidadorCancelamento {
    @Override
    public void validar(Instrucao instrucao) {
        if (instrucao.isCancelada()) {
            throw new ValidacaoException("Esta instrução já foi cancelada!");
        }
    }
}
