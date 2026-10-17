package br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.instrutor;

import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.dto.DadosEndereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Especialidade;

public record DadosDetalhamentoInstrutor(
        Long id,
        String nome,
        String email,
        String telefone,
        String cnh,
        Especialidade especialidade,
        DadosEndereco endereco,
        boolean ativo) {
}