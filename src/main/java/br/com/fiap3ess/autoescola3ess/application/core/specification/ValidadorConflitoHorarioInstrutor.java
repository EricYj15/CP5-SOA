package br.com.fiap3ess.autoescola3ess.application.core.specification;

import br.com.fiap3ess.autoescola3ess.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.application.port.out.InstrucaoRepository;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorConflitoHorarioInstrutor implements ValidadorAgendamento {
    @Autowired
    private InstrucaoRepository repository;

    @Override
    public void validar(DadosAgendamento dados) {
        boolean instrutorOcupado = repository.existsByInstrutorIdAndDataHora(
                dados.idInstrutor(),
                dados.dataHora()
        );

        if (instrutorOcupado) {
            throw new ValidacaoException("Instrutor ocupada na data/hora escolhida!");
        }
    }
}