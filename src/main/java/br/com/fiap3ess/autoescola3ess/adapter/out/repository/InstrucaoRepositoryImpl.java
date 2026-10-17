package br.com.fiap3ess.autoescola3ess.adapter.out.repository;

import br.com.fiap3ess.autoescola3ess.adapter.out.repository.entity.AlunoEntity;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.entity.InstrucaoEntity;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.entity.InstrutorEntity;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.mapper.InstrucaoEntityMapper;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.persistence.AlunoJpaRepository;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.persistence.InstrucaoJpaRepository;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.persistence.InstrutorJpaRepository;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrucao;
import br.com.fiap3ess.autoescola3ess.application.port.out.InstrucaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InstrucaoRepositoryImpl implements InstrucaoRepository {
    private final InstrucaoJpaRepository jpaRepository;
    private final AlunoJpaRepository alunoJpaRepository;
    private final InstrutorJpaRepository instrutorJpaRepository;
    private final InstrucaoEntityMapper entityMapper;

    @Override
    public Page<Instrucao> findAllByMotivoCancelamentoIsNull(Pageable paginacao) {
        return jpaRepository.findAllByMotivoCancelamentoIsNull(paginacao).map(entityMapper::toDomain);
    }

    @Override
    public boolean existsByInstrutorIdAndDataHora(Long idInstrutor, LocalDateTime dataHora) {
        return jpaRepository.existsByInstrutorIdAndDataHoraAndMotivoCancelamentoIsNull(idInstrutor, dataHora);
    }

    @Override
    public boolean existsByAlunoIdAndDataHoraBetween(
            Long idAluno,
            LocalDateTime inicioExpediente,
            LocalDateTime fimExpediente) {
        return jpaRepository.existsByAlunoIdAndDataHoraBetweenAndMotivoCancelamentoIsNull(
                idAluno,
                inicioExpediente,
                fimExpediente
        );
    }

    @Override
    public Instrucao save(Instrucao instrucao) {
        AlunoEntity aluno = alunoJpaRepository.getReferenceById(instrucao.getAluno().getId());
        InstrutorEntity instrutor = instrutorJpaRepository.getReferenceById(instrucao.getInstrutor().getId());
        InstrucaoEntity entity = entityMapper.toEntity(instrucao, aluno, instrutor);
        InstrucaoEntity saved = jpaRepository.save(entity);
        return entityMapper.toDomain(saved);
    }

    @Override
    public Optional<Instrucao> findById(Long id) {
        return jpaRepository.findById(id).map(entityMapper::toDomain);
    }
}
