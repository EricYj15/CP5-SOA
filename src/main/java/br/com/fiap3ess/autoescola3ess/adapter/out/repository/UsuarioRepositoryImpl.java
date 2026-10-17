package br.com.fiap3ess.autoescola3ess.adapter.out.repository;

import br.com.fiap3ess.autoescola3ess.adapter.out.repository.entity.UsuarioEntity;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.mapper.UsuarioEntityMapper;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.persistence.UsuarioJpaRepository;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Usuario;
import br.com.fiap3ess.autoescola3ess.application.port.out.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsuarioRepositoryImpl implements UsuarioRepository {
    private final UsuarioJpaRepository jpaRepository;
    private final UsuarioEntityMapper entityMapper;

    @Override
    public Optional<Usuario> findByLogin(String login) {
        return jpaRepository.findByLogin(login).map(entityMapper::toDomain);
    }

    @Override
    public Page<Usuario> findAllByAtivoTrue(Pageable paginacao) {
        return jpaRepository.findAllByAtivoTrue(paginacao).map(entityMapper::toDomain);
    }

    @Override
    public boolean existsByLogin(String login) {
        return jpaRepository.existsByLogin(login);
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = entityMapper.toEntity(usuario);
        UsuarioEntity saved = jpaRepository.save(entity);
        return entityMapper.toDomain(saved);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return jpaRepository.findById(id).map(entityMapper::toDomain);
    }
}
