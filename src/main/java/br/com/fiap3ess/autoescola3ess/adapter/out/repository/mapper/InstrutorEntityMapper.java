package br.com.fiap3ess.autoescola3ess.adapter.out.repository.mapper;

import br.com.fiap3ess.autoescola3ess.adapter.out.repository.entity.InstrutorEntity;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrutor;
import org.springframework.stereotype.Component;

@Component
public class InstrutorEntityMapper {
    public Instrutor toDomain(InstrutorEntity entity) {
        return new Instrutor(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getTelefone(),
                entity.getCnh(),
                entity.getEspecialidade(),
                entity.getEndereco(),
                entity.isAtivo()
        );
    }

    public InstrutorEntity toEntity(Instrutor domain) {
        return new InstrutorEntity(
                domain.getId(),
                domain.getNome(),
                domain.getEmail(),
                domain.getTelefone(),
                domain.getCnh(),
                domain.getEspecialidade(),
                domain.getEndereco(),
                domain.isAtivo()
        );
    }
}