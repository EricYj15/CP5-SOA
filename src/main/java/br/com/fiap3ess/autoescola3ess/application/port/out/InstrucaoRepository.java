package br.com.fiap3ess.autoescola3ess.application.port.out;

import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrucao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface InstrucaoRepository {
    Page<Instrucao> findAllByMotivoCancelamentoIsNull(Pageable paginacao);

    boolean existsByInstrutorIdAndDataHora(Long idInstrutor, LocalDateTime dataHora);

    boolean existsByAlunoIdAndDataHoraBetween(Long idAluno, LocalDateTime inicioExpediente, LocalDateTime fimExpediente);

    Instrucao save(Instrucao instrucao);

    Optional<Instrucao> findById(Long id);
}
