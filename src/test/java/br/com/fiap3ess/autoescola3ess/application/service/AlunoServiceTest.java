package br.com.fiap3ess.autoescola3ess.application.service;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.mapper.AlunoMapper;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.aluno.DadosAtualizacaoAluno;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.aluno.DadosCadastroAluno;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Aluno;
import br.com.fiap3ess.autoescola3ess.application.port.out.AlunoRepository;
import br.com.fiap3ess.autoescola3ess.exception.type.AlunoNotFoundException;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.Endereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.dto.DadosEndereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.mapper.EnderecoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {
    @Mock
    AlunoRepository repository;

    AlunoService service;

    @BeforeEach
    void setUp() {
        EnderecoMapper enderecoMapper = new EnderecoMapper();
        service = new AlunoService(repository, new AlunoMapper(enderecoMapper), enderecoMapper);
    }

    @Test
    @DisplayName("Expectativa: Cadastrar aluno sempre como ativo.")
    void cadastrarAlunoCenario1() {
        //arrange
        var dados = new DadosCadastroAluno("Aluno", "a@email.com.br", "11", "12345678901",
                new DadosEndereco("Rua", "1", null, "Bairro", "Cidade", "SP", "01234-567"));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        //act
        service.cadastrarAluno(dados);

        //assert
        ArgumentCaptor<Aluno> captor = ArgumentCaptor.forClass(Aluno.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isAtivo()).isTrue();
        assertThat(captor.getValue().getCpf()).isEqualTo("12345678901");
        assertThat(captor.getValue().getEndereco().getUf()).isEqualTo("SP");
    }

    @Test
    @DisplayName("Expectativa: Converter a página de domínio em página de listagem.")
    void listarAlunosCenario1() {
        when(repository.findAllByAtivoTrue(any())).thenReturn(new PageImpl<>(List.of(alunoExistente())));

        var pagina = service.listarAlunos(Pageable.unpaged());

        assertThat(pagina.getContent()).hasSize(1);
        assertThat(pagina.getContent().get(0).nome()).isEqualTo("Aluno");
    }

    @Test
    @DisplayName("Expectativa: Atualizar nome e endereço do aluno.")
    void atualizarAlunoCenario1() {
        //arrange
        Aluno aluno = alunoExistente();
        when(repository.findById(1L)).thenReturn(Optional.of(aluno));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        //act
        var resultado = service.atualizarAluno(new DadosAtualizacaoAluno(1L, "Nome Novo", null, null,
                new DadosEndereco(null, "200", null, null, "Campinas", null, null)));

        //assert
        assertThat(resultado.nome()).isEqualTo("Nome Novo");
        assertThat(resultado.endereco().numero()).isEqualTo("200");
        assertThat(resultado.endereco().cidade()).isEqualTo("Campinas");
        assertThat(resultado.endereco().logradouro()).isEqualTo("Rua");
    }

    @Test
    @DisplayName("Expectativa: Exclusão lógica deixa o aluno inativo.")
    void excluirAlunoCenario1() {
        Aluno aluno = alunoExistente();
        when(repository.findById(1L)).thenReturn(Optional.of(aluno));

        service.excluirAluno(1L);

        assertThat(aluno.isAtivo()).isFalse();
        verify(repository).save(aluno);
    }

    @Test
    @DisplayName("Expectativa: Lançar exceção ao excluir aluno inexistente.")
    void excluirAlunoCenario2() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluirAluno(99L)).isInstanceOf(AlunoNotFoundException.class);
        verify(repository, never()).save(any());
    }

    private Aluno alunoExistente() {
        return new Aluno(1L, "Aluno", "a@email.com.br", "11", "12345678901",
                new Endereco("Rua", "1", null, "Bairro", "Cidade", "SP", "01234-567"), true);
    }
}
