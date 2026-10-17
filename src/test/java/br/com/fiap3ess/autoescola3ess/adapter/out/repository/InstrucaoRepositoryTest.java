package br.com.fiap3ess.autoescola3ess.adapter.out.repository;

import br.com.fiap3ess.autoescola3ess.adapter.out.repository.entity.AlunoEntity;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.entity.InstrutorEntity;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.mapper.AlunoEntityMapper;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.mapper.InstrucaoEntityMapper;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.mapper.InstrutorEntityMapper;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrucao;
import br.com.fiap3ess.autoescola3ess.application.port.out.InstrucaoRepository;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.Endereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Especialidade;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.MotivoCancelamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({InstrucaoRepositoryImpl.class, InstrucaoEntityMapper.class, AlunoEntityMapper.class, InstrutorEntityMapper.class})
class InstrucaoRepositoryTest {
    @Autowired
    InstrucaoRepository repository;

    @Autowired
    TestEntityManager testEntity;

    @Autowired
    AlunoEntityMapper alunoMapper;

    @Autowired
    InstrutorEntityMapper instrutorMapper;

    private final LocalDateTime proximaSegundaAs10 = LocalDate.now()
            .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
            .atTime(10, 0);

    private AlunoEntity aluno;
    private InstrutorEntity instrutor;

    @BeforeEach
    void setUp() {
        Endereco endereco = new Endereco("Rua Teste", "10", null, "Centro", "São Paulo", "SP", "01234-567");
        aluno = testEntity.persist(new AlunoEntity(
                null, "Aluno Teste", "aluno@email.com.br", "(11) 98765-4321", "12345678901", endereco, true));
        instrutor = testEntity.persist(new InstrutorEntity(
                null, "Instrutor Teste", "instrutor@email.com.br", "(11) 91234-5678", "01234567890",
                Especialidade.CARROS, endereco, true));
    }

    @Test
    @DisplayName("Expectativa: Salvar a instrução e mapear aluno e instrutor para o domínio.")
    void saveCenario1() {
        //act
        Instrucao salva = repository.save(novaInstrucao(proximaSegundaAs10));
        testEntity.flush();
        testEntity.clear();

        //assert
        Instrucao encontrada = repository.findById(salva.getId()).orElseThrow();
        assertThat(encontrada.getAluno().getNome()).isEqualTo("Aluno Teste");
        assertThat(encontrada.getInstrutor().getEspecialidade()).isEqualTo(Especialidade.CARROS);
        assertThat(encontrada.getDataHora()).isEqualTo(proximaSegundaAs10);
        assertThat(encontrada.isCancelada()).isFalse();
    }

    @Test
    @DisplayName("Expectativa: Considerar o instrutor ocupado somente em instruções não canceladas.")
    void existsByInstrutorIdAndDataHoraCenario1() {
        //arrange
        Instrucao instrucao = repository.save(novaInstrucao(proximaSegundaAs10));

        //assert - ocupado
        assertThat(repository.existsByInstrutorIdAndDataHora(instrutor.getId(), proximaSegundaAs10)).isTrue();
        assertThat(repository.existsByInstrutorIdAndDataHora(instrutor.getId(), proximaSegundaAs10.plusHours(1))).isFalse();

        //act - cancelamento libera o horário
        instrucao.cancelar(MotivoCancelamento.INSTRUTOR_CANCELOU);
        repository.save(instrucao);

        //assert - livre
        assertThat(repository.existsByInstrutorIdAndDataHora(instrutor.getId(), proximaSegundaAs10)).isFalse();
    }

    @Test
    @DisplayName("Expectativa: Detectar instrução do aluno dentro do expediente do mesmo dia.")
    void existsByAlunoIdAndDataHoraBetweenCenario1() {
        repository.save(novaInstrucao(proximaSegundaAs10));

        boolean mesmoDia = repository.existsByAlunoIdAndDataHoraBetween(
                aluno.getId(), proximaSegundaAs10.withHour(6), proximaSegundaAs10.withHour(20));
        boolean outroDia = repository.existsByAlunoIdAndDataHoraBetween(
                aluno.getId(), proximaSegundaAs10.plusDays(1).withHour(6), proximaSegundaAs10.plusDays(1).withHour(20));

        assertThat(mesmoDia).isTrue();
        assertThat(outroDia).isFalse();
    }

    @Test
    @DisplayName("Expectativa: Listar apenas instruções não canceladas.")
    void findAllByMotivoCancelamentoIsNullCenario1() {
        repository.save(novaInstrucao(proximaSegundaAs10));
        Instrucao cancelada = repository.save(novaInstrucao(proximaSegundaAs10.plusDays(1)));
        cancelada.cancelar(MotivoCancelamento.OUTROS);
        repository.save(cancelada);

        var pagina = repository.findAllByMotivoCancelamentoIsNull(PageRequest.of(0, 10));

        assertThat(pagina.getContent()).hasSize(1);
        assertThat(pagina.getContent().get(0).getDataHora()).isEqualTo(proximaSegundaAs10);
    }

    private Instrucao novaInstrucao(LocalDateTime dataHora) {
        return new Instrucao(null, alunoMapper.toDomain(aluno), instrutorMapper.toDomain(instrutor), dataHora);
    }
}
