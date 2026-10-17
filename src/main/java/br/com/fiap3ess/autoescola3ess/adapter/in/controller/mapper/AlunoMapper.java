package br.com.fiap3ess.autoescola3ess.adapter.in.controller.mapper;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.aluno.DadosCadastroAluno;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.aluno.DadosDetalhamentoAluno;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.aluno.DadosListagemAluno;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Aluno;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.mapper.EnderecoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlunoMapper {
    private final EnderecoMapper enderecoMapper;

    public Aluno toDomain(DadosCadastroAluno dados) {
        return new Aluno(
                null,
                dados.nome(),
                dados.email(),
                dados.telefone(),
                dados.cpf(),
                enderecoMapper.toVo(dados.endereco()),
                true
        );
    }

    public DadosDetalhamentoAluno toDetails(Aluno domain) {
        return new DadosDetalhamentoAluno(
                domain.getId(),
                domain.getNome(),
                domain.getEmail(),
                domain.getTelefone(),
                domain.getCpf(),
                enderecoMapper.toDto(domain.getEndereco()),
                domain.isAtivo()
        );
    }

    public DadosListagemAluno toList(Aluno domain) {
        return new DadosListagemAluno(
                domain.getId(),
                domain.getNome(),
                domain.getEmail(),
                domain.getCpf()
        );
    }
}
