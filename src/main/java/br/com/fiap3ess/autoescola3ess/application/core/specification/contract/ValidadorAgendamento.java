package br.com.fiap3ess.autoescola3ess.application.core.specification.contract;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosAgendamento;

public interface ValidadorAgendamento {
    void validar(DadosAgendamento dados);
}