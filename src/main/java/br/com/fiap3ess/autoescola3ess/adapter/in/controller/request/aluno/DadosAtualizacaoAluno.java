package br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.aluno;

import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.dto.DadosEndereco;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoAluno(
        @NotNull
        Long id,
        String nome,

        @Email
        String email,
        String telefone,
        DadosEndereco endereco) {
}
