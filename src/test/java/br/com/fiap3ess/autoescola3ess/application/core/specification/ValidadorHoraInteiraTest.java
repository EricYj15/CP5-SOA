package br.com.fiap3ess.autoescola3ess.application.core.specification;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidadorHoraInteiraTest {
    private final ValidadorHoraInteira validador = new ValidadorHoraInteira();

    @Test
    @DisplayName("Expectativa: Aceitar horário cheio (ex: 10:00).")
    void validarCenario1() {
        LocalDateTime dezHoras = LocalDateTime.now().plusDays(3).withHour(10).withMinute(0);

        assertThatCode(() -> validador.validar(new DadosAgendamento(1L, 1L, null, dezHoras)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Expectativa: Rejeitar horário quebrado (ex: 10:30).")
    void validarCenario2() {
        LocalDateTime dezEMeia = LocalDateTime.now().plusDays(3).withHour(10).withMinute(30);

        assertThatThrownBy(() -> validador.validar(new DadosAgendamento(1L, 1L, null, dezEMeia)))
                .isInstanceOf(ValidacaoException.class);
    }
}
