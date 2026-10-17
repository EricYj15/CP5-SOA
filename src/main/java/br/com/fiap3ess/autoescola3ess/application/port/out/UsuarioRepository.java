package br.com.fiap3ess.autoescola3ess.application.port.out;

import br.com.fiap3ess.autoescola3ess.application.core.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UsuarioRepository {
    Optional<Usuario> findByLogin(String login);

    Page<Usuario> findAllByAtivoTrue(Pageable paginacao);

    boolean existsByLogin(String login);

    Usuario save(Usuario usuario);

    Optional<Usuario> findById(Long id);
}
