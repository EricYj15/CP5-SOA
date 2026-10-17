package br.com.fiap3ess.autoescola3ess.adapter.out.repository.persistence;

import br.com.fiap3ess.autoescola3ess.adapter.out.repository.entity.InstrucaoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InstrucaoJpaRepository extends JpaRepository<InstrucaoEntity, Long> {
    Page<InstrucaoEntity> findAllByMotivoCancelamentoIsNull(Pageable paginacao);

    boolean existsByInstrutorIdAndDataHoraAndMotivoCancelamentoIsNull(Long idInstrutor, LocalDateTime dataHora);

    boolean existsByAlunoIdAndDataHoraBetweenAndMotivoCancelamentoIsNull(
            Long idAluno,
            LocalDateTime inicioExpediente,
            LocalDateTime fimExpediente);
}
