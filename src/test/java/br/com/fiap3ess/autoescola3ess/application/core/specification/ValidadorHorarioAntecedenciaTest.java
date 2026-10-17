package br.com.fiap3ess.autoescola3ess.application.core.specification;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidadorHorarioAntecedenciaTest {
    private final ValidadorHorarioAntecedencia validador = new ValidadorHorarioAntecedencia();

    @Test
    @DisplayName("Expectativa: Rejeitar agendamento com menos de 30 minutos de antecedência.")
    void validarCenario1() {
        LocalDateTime daqui20Minutos = LocalDateTime.now().plusMinutes(20);

        assertThatThrownBy(() -> validador.validar(new DadosAgendamento(1L, 1L, null, daqui20Minutos)))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Agendamento deve respeitar antecedencia mínima de 30 minutos!");
    }

    @Test
    @DisplayName("Expectativa: Aceitar agendamento com mais de 30 minutos de antecedência.")
    void validarCenario2() {
        LocalDateTime daquiUmaHora = LocalDateTime.now().plusHours(1);

        assertThatCode(() -> validador.validar(new DadosAgendamento(1L, 1L, null, daquiUmaHora)))
                .doesNotThrowAnyException();
    }
}
