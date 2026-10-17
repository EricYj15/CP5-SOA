package br.com.fiap3ess.autoescola3ess.application.service;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.mapper.InstrutorMapper;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrutor.DadosAtualizacaoInstrutor;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrutor.DadosCadastroInstrutor;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrutor;
import br.com.fiap3ess.autoescola3ess.application.port.out.InstrutorRepository;
import br.com.fiap3ess.autoescola3ess.exception.type.InstrutorNotFoundException;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.Endereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.dto.DadosEndereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.mapper.EnderecoMapper;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Especialidade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstrutorServiceTest {
    @Mock
    InstrutorRepository repository;

    InstrutorService service;

    @BeforeEach
    void setUp() {
        EnderecoMapper enderecoMapper = new EnderecoMapper();
        service = new InstrutorService(repository, new InstrutorMapper(enderecoMapper), enderecoMapper);
    }

    @Test
    @DisplayName("Expectativa: Cadastrar instrutor ativo e devolver o ID gerado.")
    void cadastrarInstrutorCenario1() {
        //arrange
        var dados = new DadosCadastroInstrutor("Instrutor", "i@email.com.br", "11", "01234567890",
                Especialidade.VANS, new DadosEndereco("Rua", "1", null, "Bairro", "Cidade", "SP", "01234-567"));
        when(repository.save(any())).thenAnswer(inv -> {
            Instrutor i = inv.getArgument(0);
            return new Instrutor(1L, i.getNome(), i.getEmail(), i.getTelefone(), i.getCnh(),
                    i.getEspecialidade(), i.getEndereco(), i.isAtivo());
        });

        //act
        var resultado = service.cadastrarInstrutor(dados);

        //assert
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.ativo()).isTrue();
        assertThat(resultado.endereco().cidade()).isEqualTo("Cidade");
    }

    @Test
    @DisplayName("Expectativa: Atualizar apenas os campos informados, sem quebrar quando o endereço não vier.")
    void atualizarInstrutorCenario1() {
        //arrange
        Instrutor instrutor = instrutorExistente();
        when(repository.findById(1L)).thenReturn(Optional.of(instrutor));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        //act
        var resultado = service.atualizarInstrutor(
                new DadosAtualizacaoInstrutor(1L, null, null, "(11) 90000-0000", null, null));

        //assert
        assertThat(resultado.telefone()).isEqualTo("(11) 90000-0000");
        assertThat(resultado.nome()).isEqualTo("Instrutor");
        assertThat(resultado.endereco().logradouro()).isEqualTo("Rua");
    }

    @Test
    @DisplayName("Expectativa: Exclusão lógica deixa o instrutor inativo.")
    void excluirInstrutorCenario1() {
        Instrutor instrutor = instrutorExistente();
        when(repository.findById(1L)).thenReturn(Optional.of(instrutor));

        service.excluirInstrutor(1L);

        assertThat(instrutor.isAtivo()).isFalse();
        verify(repository).save(instrutor);
    }

    @Test
    @DisplayName("Expectativa: Lançar exceção ao detalhar instrutor inexistente.")
    void detalharInstrutorCenario1() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.detalharInstrutor(99L)).isInstanceOf(InstrutorNotFoundException.class);
    }

    private Instrutor instrutorExistente() {
        return new Instrutor(1L, "Instrutor", "i@email.com.br", "11", "01234567890", Especialidade.VANS,
                new Endereco("Rua", "1", null, "Bairro", "Cidade", "SP", "01234-567"), true);
    }
}
