package br.com.fiap3ess.autoescola3ess.adapter.out.client.viacep;

import br.com.fiap3ess.autoescola3ess.exception.type.CepNotFoundException;
import br.com.fiap3ess.autoescola3ess.exception.type.ServicoExternoException;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.Endereco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

//Simula as respostas do ViaCEP sem acessar a internet
class ViaCepClientTest {
    private static final String BASE_URL = "https://viacep.com.br/ws";

    private MockRestServiceServer server;
    private ViaCepClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        client = new ViaCepClient(builder.build());
    }

    @Test
    @DisplayName("Expectativa: Chamar GET /ws/{cep}/json/ e converter a resposta do ViaCEP em Endereco.")
    void buscarEnderecoPorCepCenario1() {
        //arrange
        server.expect(requestTo(BASE_URL + "/01310100/json/"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "cep": "01310-100",
                          "logradouro": "Avenida Paulista",
                          "complemento": "de 612 a 1510 - lado par",
                          "unidade": "",
                          "bairro": "Bela Vista",
                          "localidade": "São Paulo",
                          "uf": "SP",
                          "estado": "São Paulo",
                          "ibge": "3550308",
                          "ddd": "11"
                        }
                        """, MediaType.APPLICATION_JSON));

        //act
        Endereco endereco = client.buscarEnderecoPorCep("01310100");

        //assert
        server.verify();
        assertThat(endereco.getLogradouro()).isEqualTo("Avenida Paulista");
        assertThat(endereco.getBairro()).isEqualTo("Bela Vista");
        assertThat(endereco.getCidade()).isEqualTo("São Paulo");
        assertThat(endereco.getUf()).isEqualTo("SP");
        assertThat(endereco.getCep()).isEqualTo("01310-100");
        assertThat(endereco.getNumero()).isNull();
    }

    @Test
    @DisplayName("Expectativa: Lançar CepNotFoundException quando o ViaCEP responder {\"erro\": \"true\"}.")
    void buscarEnderecoPorCepCenario2() {
        server.expect(requestTo(BASE_URL + "/99999999/json/"))
                .andRespond(withSuccess("""
                        {"erro": "true"}
                        """, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.buscarEnderecoPorCep("99999999"))
                .isInstanceOf(CepNotFoundException.class)
                .hasMessage("CEP 99999999 não encontrado!");
    }

    @Test
    @DisplayName("Expectativa: Tratar também o formato antigo {\"erro\": true} (booleano).")
    void buscarEnderecoPorCepCenario3() {
        server.expect(requestTo(BASE_URL + "/99999999/json/"))
                .andRespond(withSuccess("""
                        {"erro": true}
                        """, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.buscarEnderecoPorCep("99999999"))
                .isInstanceOf(CepNotFoundException.class);
    }

    @Test
    @DisplayName("Expectativa: Lançar CepNotFoundException quando o ViaCEP responder 400.")
    void buscarEnderecoPorCepCenario4() {
        server.expect(requestTo(BASE_URL + "/00000000/json/"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.buscarEnderecoPorCep("00000000"))
                .isInstanceOf(CepNotFoundException.class);
    }

    @Test
    @DisplayName("Expectativa: Lançar ServicoExternoException quando o ViaCEP estiver fora do ar.")
    void buscarEnderecoPorCepCenario5() {
        server.expect(requestTo(BASE_URL + "/01310100/json/"))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThatThrownBy(() -> client.buscarEnderecoPorCep("01310100"))
                .isInstanceOf(ServicoExternoException.class);
    }
}
