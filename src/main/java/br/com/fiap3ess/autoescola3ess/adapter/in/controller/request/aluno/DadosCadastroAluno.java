package br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.aluno;

import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.dto.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DadosCadastroAluno(
        @NotBlank
        String nome,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String telefone,

        @NotBlank
        @Pattern(regexp = "[0-9]{11}")
        String cpf,

        @NotNull
        @Valid
        DadosEndereco endereco) {
}
