package br.com.fiap3ess.autoescola3ess.application.service;

import br.com.fiap3ess.autoescola3ess.application.port.out.ConsultaCepPort;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.Endereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.dto.DadosEndereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.mapper.EnderecoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsultaCepService {
    private final ConsultaCepPort consultaCepPort;
    private final EnderecoMapper enderecoMapper;

    public DadosEndereco consultarEndereco(String cep) {
        String cepNormalizado = cep == null ? "" : cep.replace("-", "").trim();

        if (!cepNormalizado.matches("\\d{8}")) {
            throw new ValidacaoException("CEP inválido! Informe 8 dígitos (ex: 01310-100 ou 01310100).");
        }

        Endereco endereco = consultaCepPort.buscarEnderecoPorCep(cepNormalizado);
        return enderecoMapper.toDto(endereco);
    }
}
