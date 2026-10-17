package br.com.fiap3ess.autoescola3ess.application.core.specification;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.application.port.out.AlunoRepository;
import br.com.fiap3ess.autoescola3ess.application.port.out.InstrucaoRepository;
import br.com.fiap3ess.autoescola3ess.application.port.out.InstrutorRepository;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ValidadoresComRepositorioTest {
    private static final LocalDateTime DATA_HORA = LocalDate.now().plusDays(10).atTime(10, 0);
    private static final DadosAgendamento DADOS = new DadosAgendamento(1L, 2L, null, DATA_HORA);

    @Nested
    @ExtendWith(MockitoExtension.class)
    class AlunoAtivo {
        @Mock
        AlunoRepository alunoRepository;

        @InjectMocks
        ValidadorAlunoAtivo validador;

        @Test
        @DisplayName("Expectativa: Rejeitar agendamento para aluno inativo.")
        void validarCenario1() {
            when(alunoRepository.existsByIdAndAtivoFalse(1L)).thenReturn(true);

            assertThatThrownBy(() -> validador.validar(DADOS))
                    .isInstanceOf(ValidacaoException.class)
                    .hasMessage("Não pode agendar instrução para aluno inativo!");
        }

        @Test
        @DisplayName("Expectativa: Aceitar agendamento para aluno ativo.")
        void validarCenario2() {
            when(alunoRepository.existsByIdAndAtivoFalse(1L)).thenReturn(false);

            assertThatCode(() -> validador.validar(DADOS)).doesNotThrowAnyException();
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class InstrutorAtivo {
        @Mock
        InstrutorRepository instrutorRepository;

        @InjectMocks
        ValidadorInstrutorAtivo validador;

        @Test
        @DisplayName("Expectativa: Rejeitar agendamento com instrutor inativo.")
        void validarCenario1() {
            when(instrutorRepository.existsByIdAndAtivoFalse(2L)).thenReturn(true);

            assertThatThrownBy(() -> validador.validar(DADOS)).isInstanceOf(ValidacaoException.class);
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class ConflitoHorarioInstrutor {
        @Mock
        InstrucaoRepository instrucaoRepository;

        @InjectMocks
        ValidadorConflitoHorarioInstrutor validador;

        @Test
        @DisplayName("Expectativa: Rejeitar agendamento quando o instrutor já tiver instrução no horário.")
        void validarCenario1() {
            when(instrucaoRepository.existsByInstrutorIdAndDataHora(2L, DATA_HORA)).thenReturn(true);

            assertThatThrownBy(() -> validador.validar(DADOS)).isInstanceOf(ValidacaoException.class);
        }

        @Test
        @DisplayName("Expectativa: Aceitar agendamento quando o instrutor estiver livre.")
        void validarCenario2() {
            when(instrucaoRepository.existsByInstrutorIdAndDataHora(2L, DATA_HORA)).thenReturn(false);

            assertThatCode(() -> validador.validar(DADOS)).doesNotThrowAnyException();
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class LimiteDiarioAluno {
        @Mock
        InstrucaoRepository instrucaoRepository;

        @InjectMocks
        ValidadorLimiteDiarioAluno validador;

        @Test
        @DisplayName("Expectativa: Rejeitar segunda instrução do aluno no mesmo dia, consultando o expediente inteiro.")
        void validarCenario1() {
            when(instrucaoRepository.existsByAlunoIdAndDataHoraBetween(
                    1L, DATA_HORA.withHour(6), DATA_HORA.withHour(20))).thenReturn(true);

            assertThatThrownBy(() -> validador.validar(DADOS))
                    .isInstanceOf(ValidacaoException.class)
                    .hasMessage("Aluno já possui instrução agendada para este dia!");
            verify(instrucaoRepository).existsByAlunoIdAndDataHoraBetween(1L, DATA_HORA.withHour(6), DATA_HORA.withHour(20));
        }

        @Test
        @DisplayName("Expectativa: Aceitar a primeira instrução do aluno no dia.")
        void validarCenario2() {
            when(instrucaoRepository.existsByAlunoIdAndDataHoraBetween(
                    1L, DATA_HORA.withHour(6), DATA_HORA.withHour(20))).thenReturn(false);

            assertThatCode(() -> validador.validar(DADOS)).doesNotThrowAnyException();
        }
    }
}
