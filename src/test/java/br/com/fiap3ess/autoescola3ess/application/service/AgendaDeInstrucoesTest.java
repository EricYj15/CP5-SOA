package br.com.fiap3ess.autoescola3ess.application.service;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosCancelamento;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.instrucao.DadosDetalhamentoAgendamento;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Aluno;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrucao;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrutor;
import br.com.fiap3ess.autoescola3ess.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3ess.autoescola3ess.application.core.specification.contract.ValidadorCancelamento;
import br.com.fiap3ess.autoescola3ess.application.port.out.AlunoRepository;
import br.com.fiap3ess.autoescola3ess.application.port.out.InstrucaoRepository;
import br.com.fiap3ess.autoescola3ess.application.port.out.InstrutorRepository;
import br.com.fiap3ess.autoescola3ess.exception.type.AlunoNotFoundException;
import br.com.fiap3ess.autoescola3ess.exception.type.InstrucaoNotFoundException;
import br.com.fiap3ess.autoescola3ess.exception.type.InstrutorNotFoundException;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Especialidade;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.MotivoCancelamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendaDeInstrucoesTest {
    @Mock
    AlunoRepository alunoRepository;

    @Mock
    InstrutorRepository instrutorRepository;

    @Mock
    InstrucaoRepository instrucaoRepository;

    @Mock
    ValidadorAgendamento validadorAgendamento;

    @Mock
    ValidadorCancelamento validadorCancelamento;

    AgendaDeInstrucoes agenda;

    private final LocalDateTime dataHora = LocalDateTime.now().plusDays(7).withHour(10).withMinute(0).withSecond(0).withNano(0);
    private final Aluno aluno = new Aluno(1L, "Aluno Teste", "aluno@email.com.br", "11", "12345678901", null, true);
    private final Instrutor instrutor = new Instrutor(2L, "Instrutor Teste", "instrutor@email.com.br", "11",
            "01234567890", Especialidade.CARROS, null, true);

    @BeforeEach
    void setUp() {
        agenda = new AgendaDeInstrucoes(
                alunoRepository,
                instrutorRepository,
                instrucaoRepository,
                List.of(validadorAgendamento),
                List.of(validadorCancelamento)
        );
    }

    @Test
    @DisplayName("Expectativa: Agendar com o instrutor informado, executando todos os validadores.")
    void agendarCenario1() {
        //arrange
        var dados = new DadosAgendamento(1L, 2L, null, dataHora);
        when(alunoRepository.existsById(1L)).thenReturn(true);
        when(instrutorRepository.existsById(2L)).thenReturn(true);
        when(alunoRepository.getReferenceById(1L)).thenReturn(aluno);
        when(instrutorRepository.getReferenceById(2L)).thenReturn(instrutor);
        when(instrucaoRepository.save(any())).thenAnswer(inv -> {
            Instrucao i = inv.getArgument(0);
            return new Instrucao(10L, i.getAluno(), i.getInstrutor(), i.getDataHora());
        });

        //act
        DadosDetalhamentoAgendamento resultado = agenda.agendar(dados);

        //assert
        verify(validadorAgendamento).validar(dados);
        assertThat(resultado.id()).isEqualTo(10L);
        assertThat(resultado.nomeAluno()).isEqualTo("Aluno Teste");
        assertThat(resultado.nomeInstrutor()).isEqualTo("Instrutor Teste");
        assertThat(resultado.dataHora()).isEqualTo(dataHora);
    }

    @Test
    @DisplayName("Expectativa: Escolher instrutor disponível pela especialidade quando o instrutor não for informado.")
    void agendarCenario2() {
        //arrange
        var dados = new DadosAgendamento(1L, null, Especialidade.CARROS, dataHora);
        when(alunoRepository.existsById(1L)).thenReturn(true);
        when(alunoRepository.getReferenceById(1L)).thenReturn(aluno);
        when(instrutorRepository.escolherInstrutorAleatorioDisponivel(Especialidade.CARROS, dataHora)).thenReturn(instrutor);
        when(instrucaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        //act
        agenda.agendar(dados);

        //assert
        ArgumentCaptor<Instrucao> captor = ArgumentCaptor.forClass(Instrucao.class);
        verify(instrucaoRepository).save(captor.capture());
        assertThat(captor.getValue().getInstrutor().getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Expectativa: Lançar exceção quando o aluno não existir, sem salvar nada.")
    void agendarCenario3() {
        when(alunoRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> agenda.agendar(new DadosAgendamento(1L, 2L, null, dataHora)))
                .isInstanceOf(AlunoNotFoundException.class);
        verify(instrucaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Expectativa: Lançar exceção quando o instrutor informado não existir.")
    void agendarCenario4() {
        when(alunoRepository.existsById(1L)).thenReturn(true);
        when(instrutorRepository.existsById(2L)).thenReturn(false);

        assertThatThrownBy(() -> agenda.agendar(new DadosAgendamento(1L, 2L, null, dataHora)))
                .isInstanceOf(InstrutorNotFoundException.class);
    }

    @Test
    @DisplayName("Expectativa: Exigir especialidade quando o instrutor não for informado.")
    void agendarCenario5() {
        when(alunoRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> agenda.agendar(new DadosAgendamento(1L, null, null, dataHora)))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Especialidade");
    }

    @Test
    @DisplayName("Expectativa: Lançar exceção quando nenhum instrutor estiver disponível.")
    void agendarCenario6() {
        when(alunoRepository.existsById(1L)).thenReturn(true);
        when(instrutorRepository.escolherInstrutorAleatorioDisponivel(Especialidade.MOTOS, dataHora)).thenReturn(null);

        assertThatThrownBy(() -> agenda.agendar(new DadosAgendamento(1L, null, Especialidade.MOTOS, dataHora)))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Nenhum instrutor disponível para a data/hora informada!");
        verify(instrucaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Expectativa: Não salvar quando algum validador de agendamento falhar.")
    void agendarCenario7() {
        var dados = new DadosAgendamento(1L, 2L, null, dataHora);
        when(alunoRepository.existsById(1L)).thenReturn(true);
        when(instrutorRepository.existsById(2L)).thenReturn(true);
        doThrow(new ValidacaoException("Tentativa de agendamento fora do horário de funcionamento!"))
                .when(validadorAgendamento).validar(dados);

        assertThatThrownBy(() -> agenda.agendar(dados)).isInstanceOf(ValidacaoException.class);
        verify(instrucaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Expectativa: Cancelar a instrução registrando o motivo.")
    void cancelarCenario1() {
        //arrange
        var instrucao = new Instrucao(10L, aluno, instrutor, dataHora);
        when(instrucaoRepository.findById(10L)).thenReturn(Optional.of(instrucao));

        //act
        agenda.cancelar(new DadosCancelamento(10L, MotivoCancelamento.ALUNO_DESISTIU));

        //assert
        verify(validadorCancelamento).validar(instrucao);
        verify(instrucaoRepository).save(instrucao);
        assertThat(instrucao.isCancelada()).isTrue();
        assertThat(instrucao.getMotivoCancelamento()).isEqualTo(MotivoCancelamento.ALUNO_DESISTIU);
    }

    @Test
    @DisplayName("Expectativa: Lançar exceção ao cancelar instrução inexistente.")
    void cancelarCenario2() {
        when(instrucaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agenda.cancelar(new DadosCancelamento(99L, MotivoCancelamento.OUTROS)))
                .isInstanceOf(InstrucaoNotFoundException.class);
    }

    @Test
    @DisplayName("Expectativa: Não cancelar quando algum validador de cancelamento falhar.")
    void cancelarCenario3() {
        var instrucao = new Instrucao(10L, aluno, instrutor, dataHora);
        when(instrucaoRepository.findById(10L)).thenReturn(Optional.of(instrucao));
        doThrow(new ValidacaoException("Cancelamento deve ser feito com antecedência mínima de 24 horas!"))
                .when(validadorCancelamento).validar(instrucao);

        assertThatThrownBy(() -> agenda.cancelar(new DadosCancelamento(10L, MotivoCancelamento.OUTROS)))
                .isInstanceOf(ValidacaoException.class);
        assertThat(instrucao.isCancelada()).isFalse();
        verify(instrucaoRepository, never()).save(any());
    }
}
