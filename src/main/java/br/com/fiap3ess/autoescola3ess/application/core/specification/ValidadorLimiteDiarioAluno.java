package br.com.fiap3ess.autoescola3ess.application.core.specification;

import br.com.fiap3ess.autoescola3ess.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.application.port.out.InstrucaoRepository;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ValidadorLimiteDiarioAluno implements ValidadorAgendamento {
    @Autowired
    private InstrucaoRepository repository;

    @Override
    public void validar(DadosAgendamento dados) {
        LocalDateTime inicioExpediente = dados.dataHora().withHour(6);
        LocalDateTime fimExpediente = dados.dataHora().withHour(21 - 1);

        boolean reincidencia = repository.existsByAlunoIdAndDataHoraBetween(
                dados.idAluno(),
                inicioExpediente,
                fimExpediente
        );

        if (reincidencia) {
            throw new ValidacaoException("Aluno já possui instrução agendada para este dia!");
        }
    }
}