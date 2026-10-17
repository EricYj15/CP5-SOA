package br.com.fiap3ess.autoescola3ess.application.service;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.mapper.AlunoMapper;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.aluno.DadosAtualizacaoAluno;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.aluno.DadosCadastroAluno;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.aluno.DadosDetalhamentoAluno;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.aluno.DadosListagemAluno;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Aluno;
import br.com.fiap3ess.autoescola3ess.application.port.out.AlunoRepository;
import br.com.fiap3ess.autoescola3ess.exception.type.AlunoNotFoundException;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.mapper.EnderecoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlunoService {
    private final AlunoRepository repository;
    private final AlunoMapper mapper;
    private final EnderecoMapper enderecoMapper;

    @Transactional
    public DadosDetalhamentoAluno cadastrarAluno(DadosCadastroAluno dados) {
        Aluno aluno = mapper.toDomain(dados);
        Aluno salvo = repository.save(aluno);
        return mapper.toDetails(salvo);
    }

    @Transactional(readOnly = true)
    public Page<DadosListagemAluno> listarAlunos(Pageable paginacao) {
        return repository
                .findAllByAtivoTrue(paginacao)
                .map(mapper::toList);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoAluno detalharAluno(Long id) {
        Aluno aluno = repository
                .findById(id)
                .orElseThrow(() ->
                        new AlunoNotFoundException("ID do aluno informado não existe!"));
        return mapper.toDetails(aluno);
    }

    @Transactional
    public DadosDetalhamentoAluno atualizarAluno(DadosAtualizacaoAluno dados) {
        Aluno aluno = repository
                .findById(dados.id())
                .orElseThrow(() ->
                        new AlunoNotFoundException("ID do aluno informado não existe!"));
        aluno.atualizarInformacoes(
                dados.nome(),
                dados.email(),
                dados.telefone(),
                enderecoMapper.toVo(dados.endereco())
        );
        Aluno salvo = repository.save(aluno);
        return mapper.toDetails(salvo);
    }

    @Transactional
    public void excluirAluno(Long id) {
        Aluno aluno = repository
                .findById(id)
                .orElseThrow(() ->
                        new AlunoNotFoundException("ID do aluno informado não existe!"));
        aluno.excluir();
        repository.save(aluno);
    }
}
