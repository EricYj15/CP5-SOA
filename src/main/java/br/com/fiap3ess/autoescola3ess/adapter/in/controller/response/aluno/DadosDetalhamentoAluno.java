package br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.aluno;

import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.dto.DadosEndereco;

public record DadosDetalhamentoAluno(
        Long id,
        String nome,
        String email,
        String telefone,
        String cpf,
        DadosEndereco endereco,
        boolean ativo) {
}
