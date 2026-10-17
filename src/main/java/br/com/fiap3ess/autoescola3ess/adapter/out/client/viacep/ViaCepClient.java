package br.com.fiap3ess.autoescola3ess.adapter.out.client.viacep;

import br.com.fiap3ess.autoescola3ess.adapter.out.client.viacep.dto.ViaCepResponse;
import br.com.fiap3ess.autoescola3ess.application.port.out.ConsultaCepPort;
import br.com.fiap3ess.autoescola3ess.exception.type.CepNotFoundException;
import br.com.fiap3ess.autoescola3ess.exception.type.ServicoExternoException;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.Endereco;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class ViaCepClient implements ConsultaCepPort {
    private final RestClient viaCepRestClient;

    @Override
    public Endereco buscarEnderecoPorCep(String cep) {
        ViaCepResponse response;
        try {
            response = viaCepRestClient
                    .get()
                    .uri("/{cep}/json/", cep)
                    .retrieve()
                    .body(ViaCepResponse.class);
        } catch (HttpClientErrorException ex) {
            throw new CepNotFoundException("CEP " + cep + " não encontrado!");
        } catch (RestClientException ex) {
            throw new ServicoExternoException("Serviço de consulta de CEP (ViaCEP) indisponível no momento!", ex);
        }

        if (response == null || response.cepInexistente()) {
            throw new CepNotFoundException("CEP " + cep + " não encontrado!");
        }

        return new Endereco(
                response.logradouro(),
                null,
                response.complemento(),
                response.bairro(),
                response.localidade(),
                response.uf(),
                response.cep()
        );
    }
}
