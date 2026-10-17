package br.com.fiap3ess.autoescola3ess.application.core.domain;

import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Role;

public class Usuario {
    private Long id;
    private String login;
    private String senha;
    private Role perfil;
    private boolean ativo = true;

    public Usuario() {
    }

    public Usuario(
            Long id,
            String login,
            String senha,
            Role perfil,
            boolean ativo) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    public Role getPerfil() {
        return perfil;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void atualizarInformacoes(String senhaCriptografada, Role perfil) {
        if (senhaCriptografada != null && !senhaCriptografada.isBlank()) {
            this.senha = senhaCriptografada;
        }
        if (perfil != null) {
            this.perfil = perfil;
        }
    }

    public void excluir() {
        this.ativo = false;
    }
}
