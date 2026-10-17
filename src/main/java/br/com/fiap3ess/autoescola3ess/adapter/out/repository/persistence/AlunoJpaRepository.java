package br.com.fiap3ess.autoescola3ess.adapter.out.repository.persistence;

import br.com.fiap3ess.autoescola3ess.adapter.out.repository.entity.AlunoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoJpaRepository extends JpaRepository<AlunoEntity, Long> {
    Page<AlunoEntity> findAllByAtivoTrue(Pageable paginacao);

    boolean existsByIdAndAtivoFalse(Long id);
}
