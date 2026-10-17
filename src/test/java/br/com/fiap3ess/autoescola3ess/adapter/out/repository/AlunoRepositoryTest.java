package br.com.fiap3ess.autoescola3ess.adapter.out.repository;

import br.com.fiap3ess.autoescola3ess.adapter.out.repository.mapper.AlunoEntityMapper;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Aluno;
import br.com.fiap3ess.autoescola3ess.application.port.out.AlunoRepository;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.Endereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({AlunoRepositoryImpl.class, AlunoEntityMapper.class})
class AlunoRepositoryTest {
    @Autowired
    AlunoRepository repository;

    @Autowired
    TestEntityManager testEntity;

    @Test
    @DisplayName("Expectativa: Persistir o aluno e recuperá-lo pelo ID com o endereço embutido.")
    void saveEFindByIdCenario1() {
        //arrange + act
        Aluno salvo = repository.save(novoAluno("Aluno Um", "um@email.com.br", "11111111111"));
        testEntity.flush();
        testEntity.clear();

        //assert
        Aluno encontrado = repository.findById(salvo.getId()).orElseThrow();
        assertThat(encontrado.getNome()).isEqualTo("Aluno Um");
        assertThat(encontrado.getEndereco().getCidade()).isEqualTo("São Paulo");
        assertThat(encontrado.isAtivo()).isTrue();
    }

    @Test
    @DisplayName("Expectativa: Listar apenas alunos ativos após a exclusão lógica.")
    void findAllByAtivoTrueCenario1() {
        //arrange
        repository.save(novoAluno("Aluno Ativo", "ativo@email.com.br", "22222222222"));
        Aluno inativo = repository.save(novoAluno("Aluno Inativo", "inativo@email.com.br", "33333333333"));
        inativo.excluir();
        repository.save(inativo);

        //act
        var pagina = repository.findAllByAtivoTrue(PageRequest.of(0, 10));

        //assert
        assertThat(pagina.getContent()).extracting(Aluno::getNome).containsExactly("Aluno Ativo");
    }

    @Test
    @DisplayName("Expectativa: Identificar corretamente se o aluno está inativo.")
    void existsByIdAndAtivoFalseCenario1() {
        //arrange
        Aluno ativo = repository.save(novoAluno("Aluno Ativo", "ativo2@email.com.br", "44444444444"));
        Aluno inativo = repository.save(novoAluno("Aluno Inativo", "inativo2@email.com.br", "55555555555"));
        inativo.excluir();
        repository.save(inativo);

        //act + assert
        assertThat(repository.existsByIdAndAtivoFalse(ativo.getId())).isFalse();
        assertThat(repository.existsByIdAndAtivoFalse(inativo.getId())).isTrue();
    }

    private Aluno novoAluno(String nome, String email, String cpf) {
        return new Aluno(
                null,
                nome,
                email,
                "(11) 98765-4321",
                cpf,
                new Endereco("Rua Teste", "10", null, "Centro", "São Paulo", "SP", "01234-567"),
                true
        );
    }
}
