package br.com.fiap3ess.autoescola3ess.adapter.in.controller.mapper;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.usuario.DadosCadastroUsuario;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.usuario.DadosDetalhamentoUsuario;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.usuario.DadosListagemUsuario;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    public Usuario toDomain(DadosCadastroUsuario dados, String senhaCriptografada) {
        return new Usuario(
                null,
                dados.login(),
                senhaCriptografada,
                dados.perfil(),
                true
        );
    }

    public DadosDetalhamentoUsuario toDetails(Usuario domain) {
        return new DadosDetalhamentoUsuario(
                domain.getId(),
                domain.getLogin(),
                domain.getPerfil(),
                domain.isAtivo()
        );
    }

    public DadosListagemUsuario toList(Usuario domain) {
        return new DadosListagemUsuario(
                domain.getId(),
                domain.getLogin(),
                domain.getPerfil()
        );
    }
}
