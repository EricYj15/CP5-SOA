package br.com.fiap3ess.autoescola3ess.application.port.out;

import br.com.fiap3ess.autoescola3ess.application.core.domain.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AlunoRepository {
    Page<Aluno> findAllByAtivoTrue(Pageable paginacao);

    boolean existsByIdAndAtivoFalse(Long id);

    Aluno save(Aluno aluno);

    Optional<Aluno> findById(Long id);

    boolean existsById(Long id);

    Aluno getReferenceById(Long id);
}
