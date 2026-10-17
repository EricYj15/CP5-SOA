package br.com.fiap3ess.autoescola3ess.adapter.out.repository.mapper;

import br.com.fiap3ess.autoescola3ess.adapter.out.repository.entity.UsuarioEntity;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioEntityMapper {
    public Usuario toDomain(UsuarioEntity entity) {
        return new Usuario(
                entity.getId(),
                entity.getLogin(),
                entity.getSenha(),
                entity.getPerfil(),
                entity.isAtivo()
        );
    }

    public UsuarioEntity toEntity(Usuario domain) {
        return new UsuarioEntity(
                domain.getId(),
                domain.getLogin(),
                domain.getSenha(),
                domain.getPerfil(),
                domain.isAtivo()
        );
    }
}
