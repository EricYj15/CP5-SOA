package br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.usuario;

import jakarta.validation.constraints.NotBlank;

public record DadosLogin(
        @NotBlank
        String login,

        @NotBlank
        String senha) {
}