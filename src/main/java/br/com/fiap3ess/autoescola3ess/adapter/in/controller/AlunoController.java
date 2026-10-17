package br.com.fiap3ess.autoescola3ess.adapter.in.controller;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.aluno.DadosAtualizacaoAluno;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.aluno.DadosCadastroAluno;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.aluno.DadosDetalhamentoAluno;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.aluno.DadosListagemAluno;
import br.com.fiap3ess.autoescola3ess.application.port.in.StdFeaturesPort;
import br.com.fiap3ess.autoescola3ess.application.service.AlunoService;
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
@RequestMapping("/alunos")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Alunos", description = "CRUD de alunos da auto escola (exclusão lógica)")
@RequiredArgsConstructor
public class AlunoController implements StdFeaturesPort<
        DadosCadastroAluno,
        DadosListagemAluno,
        DadosAtualizacaoAluno,
        Void,
        DadosDetalhamentoAluno,
        Long> {
    private final AlunoService service;

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar aluno", description = "Disponível apenas para o perfil ADMIN.")
    @ApiResponse(responseCode = "201", description = "Aluno cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    @ApiResponse(responseCode = "409", description = "E-mail ou CPF já cadastrado", content = @Content)
    public ResponseEntity<DadosDetalhamentoAluno> cadastrar(
            @RequestBody @Valid DadosCadastroAluno dados,
            UriComponentsBuilder uriBuilder) {
        DadosDetalhamentoAluno dto = service.cadastrarAluno(dados);
        URI uri = uriBuilder.path("/alunos/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Listar alunos ativos", description = "Listagem paginada, ordenada por nome.")
    public ResponseEntity<Page<DadosListagemAluno>> listar(
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable paginacao) {
        return ResponseEntity.ok(service.listarAlunos(paginacao));
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Detalhar aluno")
    @ApiResponse(responseCode = "200", description = "Aluno encontrado")
    @ApiResponse(responseCode = "404", description = "Aluno não encontrado", content = @Content)
    public ResponseEntity<DadosDetalhamentoAluno> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(service.detalharAluno(id));
    }

    @Override
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar aluno", description = "Atualiza apenas os campos informados. Disponível apenas para o perfil ADMIN.")
    @ApiResponse(responseCode = "200", description = "Aluno atualizado")
    @ApiResponse(responseCode = "404", description = "Aluno não encontrado", content = @Content)
    public ResponseEntity<DadosDetalhamentoAluno> atualizar(@RequestBody @Valid DadosAtualizacaoAluno dados) {
        return ResponseEntity.ok(service.atualizarAluno(dados));
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir aluno", description = "Exclusão lógica (o aluno fica inativo). Disponível apenas para o perfil ADMIN.")
    @ApiResponse(responseCode = "204", description = "Aluno excluído")
    @ApiResponse(responseCode = "404", description = "Aluno não encontrado", content = @Content)
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluirAluno(id);
        return ResponseEntity.noContent().build();
    }
}
