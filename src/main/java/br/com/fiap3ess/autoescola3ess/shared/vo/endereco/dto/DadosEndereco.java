package br.com.fiap3ess.autoescola3ess.shared.vo.endereco.dto;

import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.Endereco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DadosEndereco(
        @NotBlank
        String logradouro,
        String numero,
        String complemento,

        @NotBlank
        String bairro,

        @NotBlank
        String cidade,

        @NotBlank
        @Pattern(regexp = "[A-Z]{2}")
        String uf,

        @NotBlank
        @Pattern(regexp = "\\d{5}-\\d{3}")
        String cep) {
}