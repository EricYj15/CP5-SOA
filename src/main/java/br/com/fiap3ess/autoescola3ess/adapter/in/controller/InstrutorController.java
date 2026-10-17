package br.com.fiap3ess.autoescola3ess.adapter.in.controller;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrutor.DadosAtualizacaoInstrutor;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrutor.DadosCadastroInstrutor;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.instrutor.DadosDetalhamentoInstrutor;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.instrutor.DadosListagemInstrutor;
import br.com.fiap3ess.autoescola3ess.application.port.in.StdFeaturesPort;
import br.com.fiap3ess.autoescola3ess.application.service.InstrutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/instrutores")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Instrutores", description = "CRUD de instrutores da auto escola (exclusão lógica)")
@RequiredArgsConstructor
public class InstrutorController implements StdFeaturesPort <
        DadosCadastroInstrutor,
        DadosListagemInstrutor,
        DadosAtualizacaoInstrutor,
        Void,
        DadosDetalhamentoInstrutor,
        Long> {
    private final InstrutorService service;

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar instrutor", description = "Disponível apenas para o perfil ADMIN.")
    @ApiResponse(responseCode = "201", description = "Instrutor cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    @ApiResponse(responseCode = "409", description = "E-mail ou CNH já cadastrado", content = @Content)
    public ResponseEntity<DadosDetalhamentoInstrutor> cadastrar(
            @RequestBody @Valid DadosCadastroInstrutor dados,
            UriComponentsBuilder uriBuilder) {
        DadosDetalhamentoInstrutor dto = service.cadastrarInstrutor(dados);
        URI uri = uriBuilder.path("/instrutores/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Listar instrutores ativos", description = "Listagem paginada, ordenada por nome.")
    public ResponseEntity<Page<DadosListagemInstrutor>> listar(
        @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable paginacao) {
        return ResponseEntity.ok(service.listarInstrutores(paginacao));
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Detalhar instrutor", description = "Disponível apenas para o perfil ADMIN.")
    @ApiResponse(responseCode = "200", description = "Instrutor encontrado")
    @ApiResponse(responseCode = "404", description = "Instrutor não encontrado", content = @Content)
    public ResponseEntity<DadosDetalhamentoInstrutor> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(service.detalharInstrutor(id));
    }

    @Override
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar instrutor", description = "Atualiza apenas os campos informados. Disponível apenas para o perfil ADMIN.")
    @ApiResponse(responseCode = "200", description = "Instrutor atualizado")
    @ApiResponse(responseCode = "404", description = "Instrutor não encontrado", content = @Content)
    public ResponseEntity<DadosDetalhamentoInstrutor> atualizar(@RequestBody @Valid DadosAtualizacaoInstrutor dados) {
        return ResponseEntity.ok(service.atualizarInstrutor(dados));
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir instrutor", description = "Exclusão lógica (o instrutor fica inativo). Disponível apenas para o perfil ADMIN.")
    @ApiResponse(responseCode = "204", description = "Instrutor excluído")
    @ApiResponse(responseCode = "404", description = "Instrutor não encontrado", content = @Content)
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluirInstrutor(id);
        return ResponseEntity.noContent().build();
    }
}