package br.com.fiap3ess.autoescola3ess.adapter.in.controller;

import br.com.fiap3ess.autoescola3ess.application.service.ConsultaCepService;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.dto.DadosEndereco;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cep")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "CEP (API externa)", description = "Consulta de endereço consumindo a API pública ViaCEP (https://viacep.com.br)")
@RequiredArgsConstructor
public class CepController {
    private final ConsultaCepService service;

    @GetMapping("/{cep}")
    @Operation(
            summary = "Consultar endereço por CEP",
            description = "Consome o WebService externo ViaCEP. O retorno tem o mesmo formato do campo 'endereco' "
                    + "dos cadastros de aluno e instrutor, para o front-end preencher o formulário automaticamente.")
    @ApiResponse(responseCode = "200", description = "Endereço encontrado")
    @ApiResponse(responseCode = "400", description = "CEP em formato inválido", content = @Content)
    @ApiResponse(responseCode = "404", description = "CEP não encontrado", content = @Content)
    @ApiResponse(responseCode = "503", description = "ViaCEP indisponível", content = @Content)
    public ResponseEntity<DadosEndereco> consultarCep(
            @Parameter(description = "CEP com ou sem hífen", example = "01310-100") @PathVariable String cep) {
        return ResponseEntity.ok(service.consultarEndereco(cep));
    }
}
