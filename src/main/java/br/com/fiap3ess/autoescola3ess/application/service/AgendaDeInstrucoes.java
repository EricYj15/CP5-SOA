package br.com.fiap3ess.autoescola3ess.application.service;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosCancelamento;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.instrucao.DadosDetalhamentoAgendamento;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrucao;
import br.com.fiap3ess.autoescola3ess.application.core.specification.contract.ValidadorCancelamento;
import br.com.fiap3ess.autoescola3ess.application.port.out.InstrucaoRepository;
import br.com.fiap3ess.autoescola3ess.exception.type.InstrucaoNotFoundException;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Aluno;
import br.com.fiap3ess.autoescola3ess.exception.type.AlunoNotFoundException;
import br.com.fiap3ess.autoescola3ess.application.port.out.AlunoRepository;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrutor;
import br.com.fiap3ess.autoescola3ess.exception.type.InstrutorNotFoundException;
import br.com.fiap3ess.autoescola3ess.application.port.out.InstrutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendaDeInstrucoes {
    private final AlunoRepository alunoRepository;
    private final InstrutorRepository instrutorRepository;
    private final InstrucaoRepository repository;
    private final List<ValidadorAgendamento> validadoresAgendamento;
    private final List<ValidadorCancelamento> validadoresCancelamento;

    @Transactional
    public DadosDetalhamentoAgendamento agendar(DadosAgendamento dados) {
        if (!alunoRepository.existsById(dados.idAluno())) {
            throw new AlunoNotFoundException("ID do aluno informado não existe!");
        }
        if (dados.idInstrutor() != null && !instrutorRepository.existsById(dados.idInstrutor())) {
            throw new InstrutorNotFoundException("ID do instrutor informado não existe!");
        }
        //Validações
        validadoresAgendamento.forEach(validador -> validador.validar(dados));

        Aluno aluno = alunoRepository.getReferenceById(dados.idAluno());

        Instrutor instrutor = escolherInstrutor(dados);
        if (instrutor == null) {
            throw new ValidacaoException("Nenhum instrutor disponível para a data/hora informada!");
        }

        Instrucao instrucao = new Instrucao(null, aluno, instrutor, dados.dataHora());
        Instrucao salva = repository.save(instrucao);
        return new DadosDetalhamentoAgendamento(salva);
    }

    @Transactional(readOnly = true)
    public Page<DadosDetalhamentoAgendamento> listar(Pageable paginacao) {
        return repository
                .findAllByMotivoCancelamentoIsNull(paginacao)
                .map(DadosDetalhamentoAgendamento::new);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoAgendamento detalhar(Long id) {
        Instrucao instrucao = repository
                .findById(id)
                .orElseThrow(() ->
                        new InstrucaoNotFoundException("ID da instrução informada não existe!"));
        return new DadosDetalhamentoAgendamento(instrucao);
    }

    @Transactional
    public void cancelar(DadosCancelamento dados) {
        Instrucao instrucao = repository
                .findById(dados.idInstrucao())
                .orElseThrow(() ->
                        new InstrucaoNotFoundException("ID da instrução informada não existe!"));
        //Validações
        validadoresCancelamento.forEach(validador -> validador.validar(instrucao));

        instrucao.cancelar(dados.motivo());
        repository.save(instrucao);
    }

    private Instrutor escolherInstrutor(DadosAgendamento dados) {
        if (dados.idInstrutor() != null) {
            return instrutorRepository.getReferenceById(dados.idInstrutor());
        }
        if (dados.especialidade() == null) {
            throw new ValidacaoException("Especialidade é campo obrigatório, caso o instrutor não seja informado!");
        }
        return instrutorRepository.escolherInstrutorAleatorioDisponivel(
                dados.especialidade(),
                dados.dataHora()
        );
    }
}
