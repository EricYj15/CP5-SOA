package br.com.fiap3ess.autoescola3ess.adapter.out.repository.mapper;

import br.com.fiap3ess.autoescola3ess.adapter.out.repository.entity.AlunoEntity;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.entity.InstrucaoEntity;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.entity.InstrutorEntity;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrucao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstrucaoEntityMapper {
    private final AlunoEntityMapper alunoMapper;
    private final InstrutorEntityMapper instrutorMapper;

    public Instrucao toDomain(InstrucaoEntity entity) {
        return new Instrucao(
                entity.getId(),
                alunoMapper.toDomain(entity.getAluno()),
                instrutorMapper.toDomain(entity.getInstrutor()),
                entity.getDataHora(),
                entity.getMotivoCancelamento()
        );
    }

    public InstrucaoEntity toEntity(Instrucao domain, AlunoEntity aluno, InstrutorEntity instrutor) {
        return new InstrucaoEntity(
                domain.getId(),
                aluno,
                instrutor,
                domain.getDataHora(),
                domain.getMotivoCancelamento()
        );
    }
}
