package br.com.fiap3ess.autoescola3ess.application.core.specification;

import br.com.fiap3ess.autoescola3ess.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.application.port.out.AlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorAlunoAtivo implements ValidadorAgendamento {
    @Autowired
    private AlunoRepository alunoRepository;

    @Override
    public void validar(DadosAgendamento dados) {
        if (alunoRepository.existsByIdAndAtivoFalse(dados.idAluno())) {
            throw new ValidacaoException("Não pode agendar instrução para aluno inativo!");
        }
    }
}