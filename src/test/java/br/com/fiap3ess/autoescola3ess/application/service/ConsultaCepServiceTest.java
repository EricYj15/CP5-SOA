package br.com.fiap3ess.autoescola3ess.application.service;

import br.com.fiap3ess.autoescola3ess.application.port.out.ConsultaCepPort;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.Endereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.mapper.EnderecoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaCepServiceTest {
    @Mock
    ConsultaCepPort consultaCepPort;

    @Test
    @DisplayName("Expectativa: Remover o hífen do CEP antes de consultar a API externa.")
    void consultarEnderecoCenario1() {
        //arrange
        var service = new ConsultaCepService(consultaCepPort, new EnderecoMapper());
        when(consultaCepPort.buscarEnderecoPorCep("01310100")).thenReturn(
                new Endereco("Avenida Paulista", null, "", "Bela Vista", "São Paulo", "SP", "01310-100"));

        //act
        var endereco = service.consultarEndereco("01310-100");

        //assert
        assertThat(endereco.logradouro()).isEqualTo("Avenida Paulista");
        verify(consultaCepPort).buscarEnderecoPorCep("01310100");
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "abcdefgh", "0131010", "013101000", " "})
    @DisplayName("Expectativa: Rejeitar CEP em formato inválido sem chamar a API externa.")
    void consultarEnderecoCenario2(String cepInvalido) {
        var service = new ConsultaCepService(consultaCepPort, new EnderecoMapper());

        assertThatThrownBy(() -> service.consultarEndereco(cepInvalido)).isInstanceOf(ValidacaoException.class);
        verify(consultaCepPort, never()).buscarEnderecoPorCep(anyString());
    }
}
