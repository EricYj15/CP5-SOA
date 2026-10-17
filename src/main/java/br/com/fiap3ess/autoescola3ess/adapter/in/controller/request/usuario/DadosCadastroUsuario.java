package br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.usuario;

import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DadosCadastroUsuario(
        @NotBlank
        @Size(max = 100)
        String login,

        @NotBlank
        @Size(min = 6, max = 100)
        String senha,

        @NotNull
        Role perfil) {
}
