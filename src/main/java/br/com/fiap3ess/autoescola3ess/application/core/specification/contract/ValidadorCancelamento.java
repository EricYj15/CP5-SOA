package br.com.fiap3ess.autoescola3ess.application.core.specification.contract;

import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrucao;

public interface ValidadorCancelamento {
    void validar(Instrucao instrucao);
}
