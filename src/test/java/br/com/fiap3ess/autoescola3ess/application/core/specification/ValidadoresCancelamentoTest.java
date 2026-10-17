package br.com.fiap3ess.autoescola3ess.application.core.specification;

import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrucao;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.MotivoCancelamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidadoresCancelamentoTest {
    private final ValidadorCancelamentoAntecedencia antecedencia = new ValidadorCancelamentoAntecedencia();
    private final ValidadorCancelamentoInstrucaoAtiva instrucaoAtiva = new ValidadorCancelamentoInstrucaoAtiva();

    @Test
    @DisplayName("Expectativa: Rejeitar cancelamento com menos de 24 horas de antecedência.")
    void antecedenciaCenario1() {
        var instrucao = new Instrucao(1L, null, null, LocalDateTime.now().plusHours(23));

        assertThatThrownBy(() -> antecedencia.validar(instrucao))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Cancelamento deve ser feito com antecedência mínima de 24 horas!");
    }

    @Test
    @DisplayName("Expectativa: Aceitar cancelamento com 24 horas ou mais de antecedência.")
    void antecedenciaCenario2() {
        var instrucao = new Instrucao(1L, null, null, LocalDateTime.now().plusHours(25));

        assertThatCode(() -> antecedencia.validar(instrucao)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Expectativa: Rejeitar cancelamento de instrução já cancelada.")
    void instrucaoAtivaCenario1() {
        var instrucao = new Instrucao(1L, null, null, LocalDateTime.now().plusDays(3), MotivoCancelamento.OUTROS);

        assertThatThrownBy(() -> instrucaoAtiva.validar(instrucao))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Esta instrução já foi cancelada!");
    }

    @Test
    @DisplayName("Expectativa: Aceitar cancelamento de instrução ativa.")
    void instrucaoAtivaCenario2() {
        var instrucao = new Instrucao(1L, null, null, LocalDateTime.now().plusDays(3));

        assertThatCode(() -> instrucaoAtiva.validar(instrucao)).doesNotThrowAnyException();
    }
}
