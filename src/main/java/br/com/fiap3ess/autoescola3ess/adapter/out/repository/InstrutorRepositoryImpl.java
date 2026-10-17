package br.com.fiap3ess.autoescola3ess.adapter.out.repository;

import br.com.fiap3ess.autoescola3ess.adapter.out.repository.entity.InstrutorEntity;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.mapper.InstrutorEntityMapper;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.persistence.InstrutorJpaRepository;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrutor;
import br.com.fiap3ess.autoescola3ess.application.port.out.InstrutorRepository;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Especialidade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InstrutorRepositoryImpl implements InstrutorRepository {
    private final InstrutorJpaRepository jpaRepository;
    private final InstrutorEntityMapper entityMapper;

    @Override
    public Page<Instrutor> findAllByAtivoTrue(Pageable paginacao) {
        return jpaRepository.findAllByAtivoTrue(paginacao).map(entityMapper::toDomain);
    }

    @Override
    public Instrutor escolherInstrutorAleatorioDisponivel(Especialidade especialidade, LocalDateTime dataHora) {
        InstrutorEntity entity = jpaRepository.escolherInstrutorAleatorioDisponivel(especialidade, dataHora);
        if (entity == null) {
            return null;
        }
        return entityMapper.toDomain(entity);
    }

    @Override
    public boolean existsByIdAndAtivoFalse(Long id) {
        return jpaRepository.existsByIdAndAtivoFalse(id);
    }

    @Override
    public Instrutor save(Instrutor instrutor) {
        InstrutorEntity entity = entityMapper.toEntity(instrutor);
        InstrutorEntity saved = jpaRepository.save(entity);
        return entityMapper.toDomain(saved);
    }

    @Override
    public Optional<Instrutor> findById(Long id) {
        return jpaRepository.findById(id).map(entityMapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public Instrutor getReferenceById(Long id) {
        InstrutorEntity entity = jpaRepository.getReferenceById(id);
        return entityMapper.toDomain(entity);
    }
}