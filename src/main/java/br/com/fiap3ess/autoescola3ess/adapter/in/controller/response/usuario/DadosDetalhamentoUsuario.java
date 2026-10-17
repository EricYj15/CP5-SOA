package br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.usuario;

import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Role;

public record DadosDetalhamentoUsuario(
        Long id,
        String login,
        Role perfil,
        boolean ativo) {
}
