package br.com.fiap3ess.autoescola3ess.application.core.specification;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidadorHorarioFuncionamentoTest {
    private final ValidadorHorarioFuncionamento validador = new ValidadorHorarioFuncionamento();

    private final LocalDate proximaSegunda = LocalDate.now().plusWeeks(1).with(TemporalAdjusters.next(DayOfWeek.MONDAY));

    @ParameterizedTest
    @ValueSource(ints = {6, 12, 20})
    @DisplayName("Expectativa: Aceitar horários entre 06:00 e 20:00 (última instrução termina às 21:00).")
    void validarCenario1(int hora) {
        assertThatCode(() -> validador.validar(dados(proximaSegunda.atTime(hora, 0))))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5, 21, 23})
    @DisplayName("Expectativa: Rejeitar horários antes da abertura ou após o fechamento.")
    void validarCenario2(int hora) {
        assertThatThrownBy(() -> validador.validar(dados(proximaSegunda.atTime(hora, 0))))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Tentativa de agendamento fora do horário de funcionamento!");
    }

    @Test
    @DisplayName("Expectativa: Rejeitar agendamento no domingo.")
    void validarCenario3() {
        LocalDateTime domingo = proximaSegunda.minusDays(1).atTime(10, 0);

        assertThatThrownBy(() -> validador.validar(dados(domingo))).isInstanceOf(ValidacaoException.class);
    }

    @Test
    @DisplayName("Expectativa: Aceitar agendamento no sábado.")
    void validarCenario4() {
        LocalDateTime sabado = proximaSegunda.plusDays(5).atTime(10, 0);

        assertThatCode(() -> validador.validar(dados(sabado))).doesNotThrowAnyException();
    }

    private DadosAgendamento dados(LocalDateTime dataHora) {
        return new DadosAgendamento(1L, 1L, null, dataHora);
    }
}
