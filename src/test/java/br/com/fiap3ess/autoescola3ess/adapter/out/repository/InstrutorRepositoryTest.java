package br.com.fiap3ess.autoescola3ess.adapter.out.repository;

import br.com.fiap3ess.autoescola3ess.adapter.out.repository.entity.AlunoEntity;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.entity.InstrucaoEntity;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.entity.InstrutorEntity;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.mapper.InstrutorEntityMapper;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrutor;
import br.com.fiap3ess.autoescola3ess.application.port.out.InstrutorRepository;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.Endereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Especialidade;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.MotivoCancelamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({InstrutorRepositoryImpl.class, InstrutorEntityMapper.class})
public class InstrutorRepositoryTest {
    @Autowired
    InstrutorRepository repository;

    @Autowired
    TestEntityManager testEntity;

    private final LocalDateTime proximaSegundaAs10 = LocalDateTime
            .now()
            .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
            .withHour(10)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);

    @Test
    @DisplayName("Expectativa: Retornar null quando o instrutor estiver ocupado.")
    void escolherInstrutorAleatorioDisponivelCenario1() {
        //cadastrar aluno
        AlunoEntity aluno = cadastrarAluno();

        //cadastar instrutor
        InstrutorEntity instrutor = cadastrarInstrutor(true);

        //agendar instrução
        agendarInstrucao(aluno, instrutor, proximaSegundaAs10, null);

        //act or when
        Instrutor instrutorDisponivel = repository.escolherInstrutorAleatorioDisponivel(
                Especialidade.MOTOS,
                proximaSegundaAs10
        );
        //assert or then
        assertThat(instrutorDisponivel).isNull();
    }

    @Test
    @DisplayName("Expectativa: Retornar instrutor quando o instrutor estiver disponível.")
    void escolherInstrutorAleatorioDisponivelCenario2() {
        //cadastar instrutor
        InstrutorEntity instrutor = cadastrarInstrutor(true);

        //act or when
        Instrutor instrutorDisponivel = repository.escolherInstrutorAleatorioDisponivel(
                Especialidade.MOTOS,
                proximaSegundaAs10
        );
        //assert or then
        assertThat(instrutorDisponivel).isNotNull();
        assertThat(instrutorDisponivel.getId()).isEqualTo(instrutor.getId());
    }

    @Test
    @DisplayName("Expectativa: Retornar instrutor quando a instrução no mesmo horário foi cancelada.")
    void escolherInstrutorAleatorioDisponivelCenario3() {
        AlunoEntity aluno = cadastrarAluno();
        InstrutorEntity instrutor = cadastrarInstrutor(true);
        agendarInstrucao(aluno, instrutor, proximaSegundaAs10, MotivoCancelamento.ALUNO_DESISTIU);

        Instrutor instrutorDisponivel = repository.escolherInstrutorAleatorioDisponivel(
                Especialidade.MOTOS,
                proximaSegundaAs10
        );

        assertThat(instrutorDisponivel).isNotNull();
        assertThat(instrutorDisponivel.getId()).isEqualTo(instrutor.getId());
    }

    @Test
    @DisplayName("Expectativa: Retornar null quando o único instrutor da especialidade estiver inativo.")
    void escolherInstrutorAleatorioDisponivelCenario4() {
        cadastrarInstrutor(false);

        Instrutor instrutorDisponivel = repository.escolherInstrutorAleatorioDisponivel(
                Especialidade.MOTOS,
                proximaSegundaAs10
        );

        assertThat(instrutorDisponivel).isNull();
    }

    @Test
    @DisplayName("Expectativa: Persistir o instrutor e listar apenas os ativos.")
    void saveEFindAllByAtivoTrueCenario1() {
        Instrutor salvo = repository.save(new Instrutor(
                null, "Novo Instrutor", "novo@email.com.br", "(11) 90000-0000",
                "11122233344", Especialidade.CARROS, dadosEndereco(), true));
        cadastrarInstrutor(false);

        assertThat(salvo.getId()).isNotNull();
        assertThat(repository.existsById(salvo.getId())).isTrue();
        assertThat(repository.findAllByAtivoTrue(org.springframework.data.domain.Pageable.unpaged()))
                .extracting(Instrutor::getNome)
                .containsExactly("Novo Instrutor");
    }

    private Endereco dadosEndereco() {
        return new Endereco(
                "Rua Teste",
                "000",
                "apto. 00",
                "Vila Teste",
                "Test City",
                "TS",
                "01234-567"
        );
    }

    private AlunoEntity cadastrarAluno() {
        AlunoEntity aluno = new AlunoEntity(
                null,
                "Aluno Teste",
                "alunoteste@email.com.br",
                "(11) 98765-4321",
                "12345678901",
                dadosEndereco(),
                true
        );
        testEntity.persist(aluno);
        return aluno;
    }

    private InstrutorEntity cadastrarInstrutor(boolean ativo) {
        InstrutorEntity instrutor = new InstrutorEntity(
                null,
                "Instrutor Teste",
                "instrutorteste@email.com.br",
                "(11) 91234-5678",
                "01234567890",
                Especialidade.MOTOS,
                dadosEndereco(),
                ativo
        );
        testEntity.persist(instrutor);
        return instrutor;
    }

    private void agendarInstrucao(
            AlunoEntity aluno,
            InstrutorEntity instrutor,
            LocalDateTime dataHora,
            MotivoCancelamento motivoCancelamento) {
        InstrucaoEntity instrucao = new InstrucaoEntity(
                null,
                aluno,
                instrutor,
                dataHora,
                motivoCancelamento
        );
        testEntity.persist(instrucao);
    }
}
