package br.com.fiap3ess.autoescola3ess.adapter.out.client.viacep.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String erro) {
    //ViaCEP retorna erro=true quando o CEP não existe
    public boolean cepInexistente() {
        return "true".equalsIgnoreCase(erro);
    }
}
