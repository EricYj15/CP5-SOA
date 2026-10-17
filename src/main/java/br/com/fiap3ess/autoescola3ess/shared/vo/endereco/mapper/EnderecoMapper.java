package br.com.fiap3ess.autoescola3ess.shared.vo.endereco.mapper;

import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.Endereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.dto.DadosEndereco;
import org.springframework.stereotype.Component;

@Component
public class EnderecoMapper {
    public Endereco toVo(DadosEndereco dados) {
        if (dados == null) {
            return null;
        }
        return new Endereco(
                dados.logradouro(),
                dados.numero(),
                dados.complemento(),
                dados.bairro(),
                dados.cidade(),
                dados.uf(),
                dados.cep()
        );
    }

    public DadosEndereco toDto(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        return new DadosEndereco(
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getUf(),
                endereco.getCep()
        );
    }
}