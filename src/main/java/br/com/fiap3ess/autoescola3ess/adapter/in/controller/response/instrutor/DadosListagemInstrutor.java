package br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.instrutor;

import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Especialidade;

public record DadosListagemInstrutor(
        Long id,
        String nome,
        String email,
        Especialidade especialidade) {
}
